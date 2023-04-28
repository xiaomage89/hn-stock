package com.hn.market.indiv.controller;


import com.hn.market.common.api.CommonPage;
import com.hn.market.common.api.CommonResult;
import com.hn.market.entity.indiv.model.MkIndivDay;
import com.hn.market.entity.indiv.model.MkShareChart;
import com.hn.market.indiv.service.MkShareChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 分时图 前端控制器
 * </p>
 *
 * @author MyBatisPlus代码生成器
 * @since 2023-04-27
 */
@CrossOrigin
@Api(tags = "MkShareChartController", description = "分时图")
@RestController
@RequestMapping("/indiv/mkShareChart")
public class MkShareChartController {
    @Autowired
    private MkShareChartService mkShareChartService;


    @ApiOperation("查询个股分时图数据，sday：从前几天开始")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult list(
            @RequestParam(value = "scode", required = true) String scode,
            @RequestParam(value = "sname", required = false) String sname,
            @RequestParam(value = "sdate", required = false) String ndate,
            @RequestParam(value = "sday", required = false,defaultValue = "1") Integer sday) {
        List<MkShareChart> list = mkShareChartService.list(scode, sname, ndate, sday);
        return CommonResult.success(list);
    }
}

