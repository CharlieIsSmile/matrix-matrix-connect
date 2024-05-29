package cn.qfei.connect.model;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class WorkitemRecordsResponse {

    private String nextPageToken;
    private Boolean hasMore;
    private List<Records> records;

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class Records{
        private String primaryID;
        private Map<String, Object> data;
    }

}
