package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
//    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代金券标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;
    /**
     * 优惠券库存
     */
    private Integer amount;

    /**
     * 使用规则
     */
    private String rules;

    /**
     * 支付金额
     */
    private Integer payValue;

    /**
     * 抵扣金额
     */
    private Integer actualValue;

    /**
     * 优惠券类型
     */
    private Integer status;

    /**
     * 生效时间
     */
//    @TableField(exist = false)
    private LocalDateTime beginTime;

//    /**
//     * 失效时间
//     */
//    @TableField(exist = false)
//    private LocalDateTime endTime;

    /**
     * 创建时间
     */
//    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


//    /**
//     * 更新时间
//     */
//    private LocalDateTime updateTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;


}
