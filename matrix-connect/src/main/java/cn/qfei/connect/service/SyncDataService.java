package cn.qfei.connect.service;

import cn.qfei.connect.model.*;

public interface SyncDataService {
    /**
     * 工作项列表数据表头
     */
    TableMetaResponse tableMeta(FieldReq request);


    /**
     * 查工作项列表数据
     */
    WorkitemRecordsResponse records(RecordReq recordReq);
}
