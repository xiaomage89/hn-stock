package com.hn.market.indiv.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author majj
 * @create 2023-04-11 15:05
 */
@Data
@ApiModel(value = "MkIndivDetailVO对象", description = "分时图")
public class MkIndivDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("代码")
    private String scode;

    @ApiModelProperty("名称")
    private String sname;

    @ApiModelProperty("日期")
    private String sdate;

    @ApiModelProperty("市场")
    private Integer smarket;

    @ApiModelProperty("时间")
    private String datetime;

    @ApiModelProperty("交易价格")
    private BigDecimal price;

    @ApiModelProperty("现手")
    private BigDecimal roundLot;

    @ApiModelProperty("成交额")
    private BigDecimal amount;

    @ApiModelProperty("均价")
    private BigDecimal avgPrice;

    @ApiModelProperty("委卖")
    private BigDecimal SellVolume1;

}
