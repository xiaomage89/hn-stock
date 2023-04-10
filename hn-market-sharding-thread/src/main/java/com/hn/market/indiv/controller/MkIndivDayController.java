package com.hn.market.indiv.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hn.market.common.api.CommonPage;
import com.hn.market.common.api.CommonResult;
import com.hn.market.indiv.service.MkIndivDayService;
import com.hn.market.mbg.model.MkIndivDay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 日行情 前端控制器
 * </p>
 *
 * @author macro
 * @since 2023-04-04
 */
@RestController
@RequestMapping("/mkIndivDay")
@Api(tags = "MkIndivDayController", description = "个股行情")
public class MkIndivDayController {

    @Autowired
    private MkIndivDayService mkIndivDayService;

    /**
     * 查询最新个股行情（包含沪深京A股） ，如果数据库没有数据，网盘爬取；
     *
     * @param smarket
     * @param pageSize
     * @param pageNum
     * @return 涨跌幅，市场，代码 降序排列
     */
    @ApiOperation("查询最新个股行情（包含沪深京A股）")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MkIndivDay>> list(@RequestParam(value = "smarket", required = false) String smarket,
                                                     @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MkIndivDay> roleList = mkIndivDayService.list(smarket, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(roleList));
    }

    /**
     * 查询上市以来个股行情（K线图）（包含沪深京A股） ，如果数据库没有数据，网盘爬取；
     *
     * @param scode
     * @param sname
     * @param pageSize
     * @param pageNum
     * @return 涨跌幅，市场，代码 降序排列
     */
    @ApiOperation("查询全部个股行情（K线图）（包含沪深京A股）")
    @RequestMapping(value = "/listPast", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MkIndivDay>> listPast(@RequestParam(value = "scode", required = false) String scode,
                                                         @RequestParam(value = "sname", required = false) String sname,
                                                         @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MkIndivDay> roleList = mkIndivDayService.listPast(scode, sname, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(roleList));
    }

    @ApiOperation("根据code、date 删除后天个股信息")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult delete(@RequestParam(value = "scode", required = false) String scode,
                               @RequestParam(value = "sdate", required = true) String sdate) {
        boolean success = mkIndivDayService.delete(scode, sdate);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed();
        }
    }

}

