package cn.qfei.connect.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 字段类型枚举
 *
 * @author guanfeng
 * @date 2023/3/3 11:00
 */
public enum FieldTypeEnum {
    text(0, "单行文本"),
    textarea(1, "多行行文本"),
    radio(2, "单选按钮"),
    checkbox(3, "多选按钮"),
    select(4, "下拉单选"),
    multiple_select(5, "下拉多选"),
    person(6, "人员"),
    number(7, "数字"),
    date(8, "日期"),
    date_range(9, "日期范围"),
    cascading(10, "级联"),
    hyperlink(11, "超链接"),
    find_association(12, "查找关联(单选)"),
    encoding(13, "编码"),
    amount(16, "金额"),
    attachmentV2(18, "附件"),
    reference_field(21, "引用字段"),
    dept(22, "部门"),
    percentage(23, "百分比"),
    compute(24, "计算类型"),
    find_association_multiple(25, "查找关联(多选)");


    @EnumValue
    @JsonValue
    private Integer code;//状态编码
    private String desc;// 状态描述

    FieldTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }

    public Integer getCode() {
        return code;
    }

    public static FieldTypeEnum getByCode(Integer code) {
        FieldTypeEnum[] values = FieldTypeEnum.values();
        for (FieldTypeEnum value : values) {
            Integer tempCode = value.getCode();
            if (tempCode.equals(code)) {
                return value;
            }
        }
        return text;
    }
}

