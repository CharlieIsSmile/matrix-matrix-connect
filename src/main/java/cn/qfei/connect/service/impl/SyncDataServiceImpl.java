package cn.qfei.connect.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.qfei.connect.common.R;
import cn.qfei.connect.domain.UserConnectVoucher;
import cn.qfei.connect.model.WorkitemRecordsResponse;
import cn.qfei.connect.model.*;
import cn.qfei.connect.service.SyncDataService;

import cn.qfei.crm.client.CrmClient;
import cn.qfei.crm.request.CheckVoucherReq;
import cn.qfei.crm.response.CheckVoucherResp;
import cn.qfei.crm.response.V2EntityDetailResp;
import cn.qfei.crm.response.ViewFieldQueryResp;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SyncDataServiceImpl implements SyncDataService {

    @Resource
    private UserConnectVoucherServiceImpl userConnectVoucherService;

    @Override
    public TableMetaResponse tableMeta(FieldReq request) {

        TableMetaResponse response = new TableMetaResponse();

        FieldReq.Params params = request.toParams();
        FieldReq.DatasourceConfig datasourceConfig = params.toDatasourceConfig();

        log.info("查表结构");
        R<List<ViewFieldQueryResp>> fields = userConnectVoucherService.getFields(datasourceConfig.getVoucherId(), datasourceConfig.getEntityId(), datasourceConfig.getViewId());
        List<ViewFieldQueryResp> data = fields.getData();
        List<TableMetaResponse.Field> list = new ArrayList<>();
        data.stream().forEach(item -> {
            TableMetaResponse.Field build = new TableMetaResponse.Field();
//            build.setFieldID(item.getId().toString());
            build.setFieldID(item.getFieldKey());
            build.setFieldName(item.getFieldName());
            build.setFieldType(changeType(item.getFieldType()));
            build.setDescription(item.getFieldKey());
            build.setIsPrimary(false);
            list.add(build);
        });
        TableMetaResponse.Field build = new TableMetaResponse.Field();
        build.setFieldID("id");
        build.setFieldName("主键标识");
        build.setFieldType(1);
        build.setIsPrimary(true);
        list.add(build);
        V2EntityDetailResp detailResp = userConnectVoucherService.sysEntityDetail(datasourceConfig.getVoucherId(), datasourceConfig.getEntityId());
        response.setFields(list);
        response.setTableName(detailResp.getEntityName());
        return response;

    }

    //烽火字段属性和飞书字段属性转换
    private Integer changeType(Integer getFieldType){
        switch (getFieldType){
            case 0:
                return 1;
            case 1:
                return 1;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 1;
            case 7:
                return 2;
            case 8:
                return 5;
            case 9:
                return 1;
            case 10:
                return 1;
            case 11:
                return 1;
            case 12:
                return 3;
            case 13:
                return 1;
            case 16:
                return 8;
            case 18:
                return 1;
            case 22:
                return 1;
            case 23:
                return 1;
            case 25:
                return 4;
        }
        return 1;
    }

    private Object changeValue(ViewFieldQueryResp item,Object value){
        if (null == value || StringUtils.isBlank(value.toString()) || "null".equals(value.toString())) {
            return null;
        }
        String stringValue = value.toString();
        String fieldExt = item.getFieldExt();
        switch (item.getFieldType()){
            case 0:
            case 1:
            case 2:
            case 4:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 23:
                return stringValue;
            case 3:
            case 5:
            case 25:
                return stringValue.split(",");
            case 6:
            case 22:
                if(null==stringValue||stringValue.contains("[0]")){
                    return null;
                }else {
                    return stringValue;
                }
            case 7:
                if(StringUtils.isNotEmpty(stringValue)){
                    return Integer.valueOf(stringValue);
                }else {
                    return 0;
                }
            case 8:
                DateTime parse = DateUtil.parse(stringValue);
                return parse.getTime();
            case 16:
                return new BigDecimal(stringValue);
            case 18:
                // TODO 附件
                return 1;
            case 24:
                return 1;

        }
        return null;
    }

    @Override
    public WorkitemRecordsResponse records(RecordReq request) {
        log.info("请求参数："+JSON.toJSONString(request));
        RecordReq.Params params = request.toParams();
        RecordReq.DatasourceConfig datasourceConfig = params.toDatasourceConfig();
//        Integer maxPageSize = params.getMaxPageSize();
//        if(null==maxPageSize){
//            maxPageSize=200;
//        }
        int maxPageSize =200;
        String pageToken = params.getPageToken();

        int pageNum = StrUtil.isBlank(pageToken) ? 1 : Convert.toInt(pageToken);


        try {

//            获取该对象所有字段
            R<List<ViewFieldQueryResp>> fields = userConnectVoucherService.getFields(datasourceConfig.getVoucherId(), datasourceConfig.getEntityId(), Integer.valueOf(datasourceConfig.getViewId()));
            List<ViewFieldQueryResp> data = fields.getData();

            Map<String, ViewFieldQueryResp> settingModelMap = data.stream().collect(Collectors.toMap(ViewFieldQueryResp::getFieldKey, v -> v));

            R<Map<String, Object>> entityData = userConnectVoucherService.getEntityData(datasourceConfig.getVoucherId(), datasourceConfig.getEntityId(), datasourceConfig.getViewId(), pageNum,maxPageSize);
            Map<String, Object> data2 = entityData.getData();
            if(null==data2 || data2.isEmpty()){
                List<WorkitemRecordsResponse.Records> list = new ArrayList<>();
                WorkitemRecordsResponse response = new WorkitemRecordsResponse();
                response.setRecords(list);
                response.setNextPageToken("0");
                response.setHasMore(false);
                return response;
            }else {
                List<Map<String, Object>> condition = (List<Map<String, Object>>) data2.get("records");
                if(CollectionUtils.isEmpty(condition)){
                    List<WorkitemRecordsResponse.Records> list = new ArrayList<>();
                    WorkitemRecordsResponse response = new WorkitemRecordsResponse();
                    response.setRecords(list);
                    response.setNextPageToken("0");
                    response.setHasMore(false);
                    return response;
                }
            }
            List<Map<String, Object>> condition = (List<Map<String, Object>>) data2.get("records");
            int total = (int)data2.get("total");

            List<WorkitemRecordsResponse.Records> list = new ArrayList<>();
            if (null != condition && !condition.isEmpty()) {//数据集合
                condition.stream().forEach(item -> {//获取每一条记录的主键
                    String id = item.get("id").toString();
                    Map<String,Object> data1 = new HashMap<>();
                    item.entrySet().stream().forEach(entry -> {
                        ViewFieldQueryResp viewFieldQueryResp = settingModelMap.get(entry.getKey());
                        if(viewFieldQueryResp!=null && !viewFieldQueryResp.getFieldType().equals(18)) {
//                            data1.put(viewFieldQueryResp.getId().toString(), changeValue(viewFieldQueryResp,entry.getValue()));
                            data1.put(viewFieldQueryResp.getFieldKey(), changeValue(viewFieldQueryResp,entry.getValue()));
                        }else {
                            if(entry.getKey().equals("id")){
                                data1.put("id",entry.getValue().toString());
                            }
                        }
                    });
                    WorkitemRecordsResponse.Records build1 = WorkitemRecordsResponse.Records.builder().primaryID(id).data(data1).build();
                    list.add(build1);
                });
            }

            if(!list.isEmpty()){
                WorkitemRecordsResponse response = new WorkitemRecordsResponse();
                response.setRecords(list);
                response.setNextPageToken(String.valueOf(pageNum+1));
                response.setHasMore(pageNum * maxPageSize <= total);
                return response;
            }

            log.info("请求参数："+JSON.toJSONString(request));
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }
}
