package cn.qfei.connect.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 授权租户表
 * @TableName user_connect_voucher
 */
@TableName(value ="user_connect_voucher")
@Data
public class UserConnectVoucher implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String userName;

    /**
     * 授权凭证
     */
    private String voucher;

    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更改时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * tenant_id
     */
    private String tenantId;

    /**
     * userid
     */
    private String userId;

    /**
     * 是否已删除 1:是 0：否
     */
    private Boolean isDeleted;

    /**
     * 烽火租户id
     */
    private Long orgId;

    /**
     * 是否有效：1:是 0：否
     */
    private Boolean status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}