package com.hn.market.indiv.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hn.market.mbg.model.MkIndivDay;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 日行情 服务类
 * </p>
 *
 * @author macro
 * @since 2023-04-04
 */
public interface MkIndivDayService extends IService<MkIndivDay> {
    /**
     * 查询最新个股行情（包含沪深京A股） ，如果数据库没有数据，网盘爬取
     * @param smarket
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<MkIndivDay> list(String smarket, Integer pageSize, Integer pageNum);

    /**
     * 查询上市以来所有个股行情（包含沪深京A股） ，如果数据库没有数据，网盘爬取；
     * @param scode
     * @param sname
     * @param pageSize
     * @param pageNum
     * @return 涨跌幅，市场，代码 降序排列
     */
    List<MkIndivDay>  listPast(String scode,String sname,Integer pageSize, Integer pageNum);

    boolean delete(String scode, String sdate);
}
