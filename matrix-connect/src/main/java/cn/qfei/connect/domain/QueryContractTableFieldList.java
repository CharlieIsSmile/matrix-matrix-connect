package cn.qfei.connect.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryContractTableFieldList {


    private String nextPageToken;

    private Boolean hasMore;

    private List<Map<String,Object>> data;

}
