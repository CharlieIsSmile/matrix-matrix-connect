package cn.qfei.connect.common;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ContractVoucherParam {

    /**
     * id
     */
    private Integer id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名userName不能为空")
    private String userName;

    /**
     * 凭证
     */
    @NotBlank(message = "凭证voucher不能为空")
    private String voucher;

    /**
     * tenantId
     */
    @NotBlank(message = "tenantId不能为空")
    private String tenantId;

    /**
     * userId
     */
    @NotBlank(message = "userId不能为空")
    private String userId;
}
