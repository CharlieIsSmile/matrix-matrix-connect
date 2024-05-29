package cn.qfei.connect.controller;

import cn.hutool.extra.servlet.ServletUtil;
import cn.qfei.connect.common.R;
import cn.qfei.connect.model.FieldReq;
import cn.qfei.connect.model.TableMetaResponse;
import cn.qfei.connect.service.SyncDataService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

/**
 * 获取表头控制器
 */
@RestController
@RequestMapping("/tableMeta")
@Slf4j
public class TableMetaController {

    @Resource
    private SyncDataService syncDataService;

    /**
     * 获取表结构字段
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/api/tableMeta")
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
}
