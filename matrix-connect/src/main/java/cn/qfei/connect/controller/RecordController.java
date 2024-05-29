package cn.qfei.connect.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.qfei.connect.common.R;
import cn.qfei.connect.model.RecordReq;
import cn.qfei.connect.model.WorkitemRecordsResponse;
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
 * 同步数据控制器
 */
@RestController
@RequestMapping("/records")
@Slf4j
public class RecordController {

    @Resource
    private SyncDataService syncDataService;


    /**
     * 同步数据
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/api/records")
    public R<WorkitemRecordsResponse> records(HttpServletRequest httpServletRequest){
        TimeInterval timer = DateUtil.timer();
        String timestamp = ServletUtil.getHeader(httpServletRequest, "X-Base-Request-Timestamp", Charset.defaultCharset());
        String nonce = ServletUtil.getHeader(httpServletRequest, "X-Base-Request-Nonce", Charset.defaultCharset());
        String body = ServletUtil.getBody(httpServletRequest);
        RecordReq request = JSON.parseObject(body, RecordReq.class);
        RecordReq.Params params = request.toParams();
        RecordReq.DatasourceConfig datasourceConfig = params.toDatasourceConfig();
        log.info("[查数据] 请求：req = {} header:{} {}", JSON.toJSONString(request), timestamp, nonce);

        Integer dataType = datasourceConfig.getDataType();
        WorkitemRecordsResponse data = null;
        if(dataType == null || dataType == 0){
            // 列表查询
            data = syncDataService.records(request);
        }
        log.info("[查数据] 响应，耗时：{}ms：res = {}", timer.interval(), StrUtil.subPre(JSON.toJSONString(data), 500));
        return R.data(data);
    }
}
