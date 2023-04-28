package com.hn.market.indiv.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hn.market.common.api.CommonPage;
import com.hn.market.common.api.CommonResult;
import com.hn.market.entity.indiv.model.MkIndivDay;
import com.hn.market.indiv.service.MkIndivDayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 个股行情 前端控制器
 * </p>
 *
 * @author macro
 * @since 2023-04-04
 */
@CrossOrigin
@RestController
@RequestMapping("/mkIndivDay")
@Api(tags = "MkIndivDayController", description = "个股行情")
public class MkIndivDayController {

    @Autowired
    private MkIndivDayService mkIndivDayService;

    @ApiOperation("查询个股行情（包含沪深京A股）")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MkIndivDay>> list(@RequestParam(value = "smarket", required = false) String smarket,
                                                       @RequestParam(value = "scode", required = false) String scode,
                                                       @RequestParam(value = "sname", required = false) String sname,
                                                       @RequestParam(value = "sdate", required = false) String sdate,
                                                        @RequestParam(value = "edate", required = false) String edate,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MkIndivDay> list = mkIndivDayService.list(smarket,scode,sname,sdate,edate,pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(list,pageSize,pageNum));
    }

    @ApiOperation("爬取最新个股行情（包含沪深京A股）")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MkIndivDay>> create(@RequestParam(value = "smarket", required = false) String smarket,
                                                     @RequestParam(value = "scode", required = false) String scode,
                                                     @RequestParam(value = "sname", required = false) String sname,
                                                     @RequestParam(value = "sdate", required = false) String sdate,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<MkIndivDay> page = mkIndivDayService.create(smarket,scode,sname,sdate,pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(page));
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
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<MkIndivDay> roleList = null;
        try {
            roleList = mkIndivDayService.listPast(scode, sname, pageSize, pageNum);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CommonResult.success(CommonPage.restPage(roleList));
    }

    @ApiOperation("根据code、date 删除个股信息")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult delete(@RequestParam(value = "scode", required = false) String scode,
                               @RequestParam(value = "sname", required = false) String sname,
                               @RequestParam(value = "sdate", required = true) String sdate) {
        boolean success = mkIndivDayService.delete(scode, sname,sdate);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed();
        }
    }

    /**
     *
     * @param scode
     * @param sname
     * @param ndays   几天的分时图数据，最大值5天
     * @return
     */
    @ApiOperation("爬取分时图信息")
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult createDetails(@RequestParam(value = "scode", required = false) String scode,
                               @RequestParam(value = "sname", required = false) String sname,
                               @RequestParam(value = "ndays", required = true,defaultValue = "1") String ndays) {
        boolean success = mkIndivDayService.createDetails(scode, sname,ndays);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed();
        }
    }
}

