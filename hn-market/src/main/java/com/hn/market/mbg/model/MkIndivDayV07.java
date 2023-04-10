package com.hn.market.mbg.model;

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
 * 日行情
 * </p>
 *
 * @author macro
 * @since 2023-04-04
 */
@Getter
@Setter
@TableName("mk_indiv_day_v07")
@ApiModel(value = "MkIndivDayV07对象", description = "日行情")
public class MkIndivDayV07 implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键号")
    @TableId(value = "TENANT_ID", type = IdType.AUTO)
    private Integer tenantId;

    @ApiModelProperty("代码")
    private String scode;

    @ApiModelProperty("名称")
    private String sname;

    @ApiModelProperty("日期")
    private String sdate;

    @ApiModelProperty("种类")
    private String sclass;

    @ApiModelProperty("开盘")
    private BigDecimal open;

    @ApiModelProperty("收盘")
    private BigDecimal close;

    @ApiModelProperty("最高")
    private BigDecimal high;

    @ApiModelProperty("最低")
    private BigDecimal low;

    @ApiModelProperty("昨收")
    private BigDecimal preclose;

    @ApiModelProperty("成交量(手)")
    private Integer volume;

    @ApiModelProperty("成交额")
    private BigDecimal amount;

    @ApiModelProperty("振幅%")
    private BigDecimal amplitude;

    @ApiModelProperty("涨跌幅(%)")
    private BigDecimal differrange;

    @ApiModelProperty("涨跌额")
    private BigDecimal differ;

    @ApiModelProperty("换手率%")
    private BigDecimal turn;

    @ApiModelProperty("市盈率(动态)")
    private BigDecimal pe;

    @ApiModelProperty("市盈率TTM")
    private BigDecimal pettm;

    @ApiModelProperty("量比")
    private BigDecimal volratio;

    @ApiModelProperty("总市值")
    private BigDecimal mv;

    @ApiModelProperty("流通市值")
    private BigDecimal liqmv;

    @ApiModelProperty("市净")
    private BigDecimal pb;

    @ApiModelProperty("今日主力净流入")
    private BigDecimal netinflow;


}
