package cn.qfei.connect.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class RecordReq {
    private String params;
    private String context;

    public Params toParams(){
        if(StrUtil.isBlank(this.params)){
            return new Params();
        }
        return JSONUtil.toBean(this.params, Params.class);
    }
    public Context toContext(){
        if(StrUtil.isBlank(this.context)){
            return new Context();
        }
        return JSONUtil.toBean(this.context, Context.class);
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class Params {
        private String datasourceConfig;
        private String transactionID;
        private String pageToken;
        private Integer maxPageSize;

        public DatasourceConfig toDatasourceConfig(){
            if(StrUtil.isBlank(datasourceConfig)){
                return new DatasourceConfig();
            }
            return JSONUtil.toBean(this.datasourceConfig, DatasourceConfig.class);
        }
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class DatasourceConfig {
        private Integer entityId;
        private Integer viewId;
        private Integer dataType;
        private Integer voucherId;
        private List<String> fields;
        private String number;
        private String projectKey;
        private String typeKey;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class Context{
        private Bitable bitable;
        private String packID;
        private String type;
        private String tenantKey;
        private String userTenantKey;
        private String bizInstanceID;
        private ScriptArgs scriptArgs;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class Bitable{
        private String token;
        private String logID;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class ScriptArgs{
        private String projectURL;
        private String baseOpenID;
    }
}
