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
 * 成交明细
 * </p>
 *
 * @author macro
 * @since 2023-04-04
 */
@Getter
@Setter
@TableName("mk_tran_detail")
@ApiModel(value = "MkTranDetail对象", description = "成交明细")
public class MkTranDetail implements Serializable {

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

    @ApiModelProperty("时间")
    private String stime;

    @ApiModelProperty("成交价")
    private BigDecimal price;

    @ApiModelProperty("手数")
    private Integer lots;

    @ApiModelProperty("笔数")
    private Integer num;


}
