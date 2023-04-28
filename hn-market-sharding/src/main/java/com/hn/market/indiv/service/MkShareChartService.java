package com.hn.market.indiv.service;

import com.hn.market.entity.indiv.model.MkIndivDay;
import com.hn.market.entity.indiv.model.MkShareChart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 分时图 服务类
 * </p>
 *
 * @author MyBatisPlus代码生成器
 * @since 2023-04-27
 */
public interface MkShareChartService extends IService<MkShareChart> {

    List<MkShareChart> list( String scode, String sname, String ndate, int sday);

}
