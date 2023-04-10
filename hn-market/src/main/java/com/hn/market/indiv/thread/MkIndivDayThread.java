package com.hn.market.indiv.thread;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hn.market.mbg.mapper.MkIndivDayMapper;
import com.hn.market.mbg.model.MkIndivDay;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author majj
 * @create 2023-04-04 16:01
 */
@Component
public class MkIndivDayThread extends ServiceImpl<MkIndivDayMapper, MkIndivDay> {

    @Async("myAsync")
    public void insertMk(List<JSONObject> diffList, String sdate, CountDownLatch countDownLatch) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("Stcok自带的线程池" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
            List list = new ArrayList<MkIndivDay>();
            for (JSONObject stock : diffList) {
                MkIndivDay mkIndivDay = new MkIndivDay();
                //日期
                mkIndivDay.setSdate(sdate);
                //收盘
                mkIndivDay.setClose(stock.getBigDecimal("f2", BigDecimal.ZERO));
                //涨跌幅(%)
                mkIndivDay.setDifferrange(stock.getBigDecimal("f3", BigDecimal.ZERO));
                //涨跌额
                mkIndivDay.setDiffer(stock.getBigDecimal("f4", BigDecimal.ZERO));
                //成交量(手)
                mkIndivDay.setVolume(stock.getInt("f5", 0));
                //成交额
                mkIndivDay.setAmount(stock.getBigDecimal("f6", BigDecimal.ZERO));
                //振幅%
                mkIndivDay.setAmplitude(stock.getBigDecimal("f7", BigDecimal.ZERO));
                //换手率%
                mkIndivDay.setTurn(stock.getBigDecimal("f8", BigDecimal.ZERO));
                //市盈率(动态)
                mkIndivDay.setPe(stock.getBigDecimal("f9", BigDecimal.ZERO));
                //量比
                mkIndivDay.setVolratio(stock.getBigDecimal("f10", BigDecimal.ZERO));
                //代码
                mkIndivDay.setScode(stock.getStr("f12", null));
                //市场
                mkIndivDay.setSclass(stock.getStr("f13", null));
                //名称
                mkIndivDay.setSname(stock.getStr("f14", null));
                //最高
                mkIndivDay.setHigh(stock.getBigDecimal("f15", BigDecimal.ZERO));
                //最低
                mkIndivDay.setLow(stock.getBigDecimal("f16", BigDecimal.ZERO));
                //开盘
                mkIndivDay.setOpen(stock.getBigDecimal("f17", BigDecimal.ZERO));
                //昨收
                mkIndivDay.setPreclose(stock.getBigDecimal("f18", BigDecimal.ZERO));
                //总市值
                mkIndivDay.setMv(stock.getBigDecimal("f20", BigDecimal.ZERO));
                //流通市值
                mkIndivDay.setLiqmv(stock.getBigDecimal("f21", BigDecimal.ZERO));
                //市净
                mkIndivDay.setPb(stock.getBigDecimal("f23", BigDecimal.ZERO));
                //今日主力净流入
                mkIndivDay.setNetinflow(stock.getBigDecimal("f62", BigDecimal.ZERO));
                //TTM市盈率
                mkIndivDay.setPettm(stock.getBigDecimal("f115", BigDecimal.ZERO));

                list.add(mkIndivDay);
            }
            if (list != null && list.size() > 0) {
                saveBatch(list);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    @Async("myAsync")
    public void insertMkPast(List<String> klines, List dates, String scode, String smarket, String sname, CountDownLatch countDownLatch) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("Stcok自带的线程池" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
            MkIndivDay mkIndivDay = new MkIndivDay();
            List<MkIndivDay> list = new ArrayList<MkIndivDay>();
            for (String kline : klines) {
                kline.replaceAll("\"", "");
                String[] split = kline.split(",");

                mkIndivDay = new MkIndivDay();
                //日期
                String sdate = split[0].replace("-", "");
                //已经存在日期，不保存
                if (dates.contains(sdate)) continue;

                mkIndivDay.setSdate(sdate);
                //收盘
                mkIndivDay.setClose(new BigDecimal(split[2]));
                //涨跌幅(%)
                mkIndivDay.setDifferrange(new BigDecimal(split[8]));
                //涨跌额
                mkIndivDay.setDiffer(new BigDecimal(split[9]));
                //成交量(手)
                mkIndivDay.setVolume(Integer.valueOf(split[5]));
                //成交额
                mkIndivDay.setAmount(new BigDecimal(split[6]));
                //振幅%
                mkIndivDay.setAmplitude(new BigDecimal(split[7]));
                //换手率%
                mkIndivDay.setTurn(new BigDecimal(split[10]));

                //代码
                mkIndivDay.setScode(scode);
                //市场
                mkIndivDay.setSclass(smarket);
                //名称
                mkIndivDay.setSname(sname);
                //最高
                mkIndivDay.setHigh(new BigDecimal(split[3]));
                //最低
                mkIndivDay.setLow(new BigDecimal(split[4]));
                //开盘
                mkIndivDay.setOpen(new BigDecimal(split[1]));

                list.add(mkIndivDay);
            }
            if (list != null && list.size() > 0) {
                saveBatch(list);
            }
        } finally {
            countDownLatch.countDown();
        }
    }
}
