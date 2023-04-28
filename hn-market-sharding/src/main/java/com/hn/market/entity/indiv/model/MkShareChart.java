package com.hn.market.entity.indiv.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 分时图
 * </p>
 *
 * @author MyBatisPlus代码生成器
 * @since 2023-04-27
 */
@Getter
@Setter
@TableName("mk_share_chart")
@ApiModel(value = "MkShareChart对象", description = "分时图")
public class MkShareChart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键号")
    @TableId(value = "TENANT_ID", type = IdType.AUTO)
    private Long tenantId;

    @ApiModelProperty("代码")
    private String scode;

    @ApiModelProperty("时间")
    private String stime;

    @ApiModelProperty("成交价")
    private BigDecimal price;

    @ApiModelProperty("分时量")
    private Integer num;

    @ApiModelProperty("成交额")
    private BigDecimal amount;

    @ApiModelProperty("均价")
    private BigDecimal avgPrice;

    @ApiModelProperty("委买笔数")
    private Integer commissionBuyNum;


}
