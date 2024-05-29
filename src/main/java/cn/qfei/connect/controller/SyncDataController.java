package cn.qfei.connect.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.servlet.ServletUtil;
import cn.qfei.connect.common.R;
import cn.qfei.connect.domain.UserConnectVoucher;
import cn.qfei.connect.model.WorkitemRecordsResponse;
import cn.qfei.connect.model.*;
import cn.qfei.connect.service.SyncDataService;
import cn.qfei.connect.service.impl.UserConnectVoucherServiceImpl;
import cn.qfei.crm.client.CrmClient;
import cn.qfei.crm.request.CheckVoucherReq;
import cn.qfei.crm.response.CheckVoucherResp;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 烽火连接器同步飞书数据
 */
@RestController
@RequestMapping("/syncData")
@Slf4j
public class SyncDataController {

    @Resource
    private SyncDataService syncDataService;

    @Value("${data.config.uri}")
    private String dataSourceConfigUiUri;

    @Resource
    private UserConnectVoucherServiceImpl userConnectVoucherService;

    /**
     * 获取表结构字段
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/tableMeta")
    public R<TableMetaResponse> tableMeta(HttpServletRequest httpServletRequest){
        String timestamp = ServletUtil.getHeader(httpServletRequest, "X-Base-Request-Timestamp", Charset.defaultCharset());
        String nonce = ServletUtil.getHeader(httpServletRequest, "X-Base-Request-Nonce", Charset.defaultCharset());
        String body = ServletUtil.getBody(httpServletRequest);
        FieldReq request = JSON.parseObject(body, FieldReq.class);
        log.info("[tableMeta] 请求：req = {} header:{} {}", JSON.toJSONString(request), timestamp, nonce);
        FieldReq.DatasourceConfig datasourceConfig = request.toParams().toDatasourceConfig();
        Integer dataType = datasourceConfig.getDataType();
        TableMetaResponse data = null;
        if(dataType == null || dataType == 0){
            // 列表查询
            data = syncDataService.tableMeta(request);
        }

        log.info("[tableMeta] 请求：res = {}", JSON.toJSONString(data));
        return R.data(data);
    }

    /**
     * 同步数据
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/records")
    public R<WorkitemRecordsResponse> records(HttpServletRequest httpServletRequest){
        TimeInterval timer = DateUtil.timer();
        String timestamp = ServletUtil.getHeader(httpServletRequest, "X-Base-Request-Timestamp", Charset.defaultCharset());
        String nonce = ServletUtil.getHeader(httpServletRequest, "X-Base-Request-Nonce", Charset.defaultCharset());
        String body = ServletUtil.getBody(httpServletRequest);
        RecordReq request = JSON.parseObject(body, RecordReq.class);
        RecordReq.Params params = request.toParams();
        RecordReq.DatasourceConfig datasourceConfig = params.toDatasourceConfig();
        log.info("[records] 请求：req = {} header:{} {}", JSON.toJSONString(request), timestamp, nonce);

        Integer dataType = datasourceConfig.getDataType();
        UserConnectVoucher ucv = userConnectVoucherService.selectOneByVoucherId(datasourceConfig.getVoucherId());
//        Assert.notNull(ucv, "凭证不存在");
        if(Objects.isNull(ucv)){
            return R.fail("凭证无效",1254500);
        }

        //校验凭证是否有效
        CrmClient client = CrmClient.getClient(null,null);
        List<String> vouchers = new ArrayList<>();
        vouchers.add(ucv.getVoucher());
        CheckVoucherReq req = CheckVoucherReq.builder().vouchers(vouchers).build();
        List<CheckVoucherResp> check = client.voucher().check(req);
        if(CollectionUtils.isEmpty(check)){
            return R.fail("凭证无效",1254500);
        }
        Assert.notNull(ucv, "凭证无效");
        if(!check.get(0).getVoucherStatus().equals("1")){
//            throw new IllegalArgumentException("凭证无效");
            return R.fail("凭证无效",1254500);
        }
        WorkitemRecordsResponse data = null;
        if(dataType == null || dataType == 0){
            // 列表查询
            data = syncDataService.records(request);
        }
        log.info("[records] 响应，耗时：{}ms：res = {}", timer.interval(), JSON.toJSONString(data));
        return R.data(data);
    }

    /**
     * 获取json文件
     */
    @GetMapping("/meta.json")
    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("schemaVersion",1);
        jsonObject.put("version","1.0.0");
        jsonObject.put("type","data_connector");
        JSONObject obj1 = new JSONObject();
        obj1.put("dataSourceConfigUiUri",dataSourceConfigUiUri);
        obj1.put("disabledPeriodicSync",false);
        jsonObject.put("extraData",obj1);
        JSONObject obj2 = new JSONObject();
        obj2.put("type","http");
        JSONObject obj3 = new JSONObject();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("type","tableMeta");
        map1.put("uri","/tableMeta");
        list.add(map1);
        Map<String,Object> map2 = new HashMap<>();
        map2.put("type","records");
        map2.put("uri","/records");
        list.add(map2);
        obj3.put("uris",list);
        obj2.put("httpProtocol",obj3);
        jsonObject.put("protocol",obj2);
        return jsonObject;
    }

}
