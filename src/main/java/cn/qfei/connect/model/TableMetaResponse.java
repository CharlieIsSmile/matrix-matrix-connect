package cn.qfei.connect.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Data
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Slf4j
public class TableMetaResponse {
    private String tableName;
    private List<Field> fields;

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @Accessors(chain = true)
    public static class Field{
        private String fieldID;
        private String fieldName;
        private Integer fieldType;
        private Boolean isPrimary;
        private String description;
        private Object property;

        public Field(String fieldID, String fieldName, Integer fieldType,
                     Boolean isPrimary, String description, Object property) {
            this.fieldID = fieldID;
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.isPrimary = isPrimary;
            this.description = description;
            this.property = property;
        }
    }
}
