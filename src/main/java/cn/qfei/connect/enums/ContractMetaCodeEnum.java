package cn.qfei.connect.enums;

import lombok.Getter;

/**
 * 关联接口和原本实体类
 *
 * @author whj
 */

@Getter
public enum ContractMetaCodeEnum {


    textarea(1, "多行文本","1"),
    number(2, "数字","2"),
    radio(3, "单选","3"),
    checkbox(4, "多选",""),
    date(5, "日期","5"),
    BARCODE(6, "条码","1"),
    CHECKBOX(7, "复选框","7"),
    CURRENCY(8, "金额","2"),
    PHONE(9, "电话号码","13"),
    URL(10, "超链接","15"),
    PROGRESS(11, "进度","2"),
    RATING(12, "评分","2"),
    COMPLETED(13, "地理位置","22");

    private int code;
    private String test;
    private String codeString;

    ContractMetaCodeEnum(int code, String test, String codeString) {
        this.code = code;
        this.test = test;
        this.codeString = codeString;
    }

    public static ContractMetaCodeEnum getByCode(int code) {
        for (ContractMetaCodeEnum en : ContractMetaCodeEnum.values()) {
            if (code == en.getCode()) {
                return en;
            }
        }
        return null;
    }

}
