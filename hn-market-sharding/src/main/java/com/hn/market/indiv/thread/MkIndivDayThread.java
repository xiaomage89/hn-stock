package com.hn.market.indiv.thread;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hn.market.mbg.mapper.MkIndivDayMapper;
import com.hn.market.mbg.model.MkIndivDay;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @author majj
 * @create 2023-04-04 16:01
 */
@Component
public class MkIndivDayThread extends ServiceImpl<MkIndivDayMapper, MkIndivDay> {

    @Async("myAsync")
    public Future<Map<String, List>> getScodeSdate(List<MkIndivDay> list){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("Stcok自带的线程池开始" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
            //查看股票数据的日期；
            Map<String, List> map = new HashMap<>();
            Map<Integer, List<String>> map0 = new HashMap<>();
            Map<Integer, List<String>> map1 = new HashMap<>();
            QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
            LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
            for (MkIndivDay mk : list) {
                int code = Integer.valueOf(mk.getScode());
                int key = code % 20;
                if (0==(mk.getSmarket())) {
                    if (map0.containsKey(key)) {
                        List<String> codes = map0.get(key);
                        codes.add(mk.getScode());
                    } else {
                        List codes = new ArrayList<>();
                        codes.add(mk.getScode());
                        map0.put(key, codes);
                    }
                }
                if (1==(mk.getSmarket())) {
                    if (map1.containsKey(key)) {
                        List<String> codes = map1.get(key);
                        codes.add(mk.getScode());
                    } else {
                        List codes = new ArrayList<>();
                        codes.add(mk.getScode());
                        map1.put(key, codes);
                    }
                }
            }

            for (Integer key: map0.keySet()) {
                List<String> codes = map0.get(key);
                lambda.clear();
                lambda.select(MkIndivDay::getScode, MkIndivDay::getSdate);
                lambda.eq(MkIndivDay::getSmarket, 0);
                lambda.in(MkIndivDay::getScode, codes);
                List<MkIndivDay> datelist = list(lambda);
                List dates = new ArrayList<String>();
                String oldkey = "";
                for (MkIndivDay data : datelist) {
                    if (oldkey.equals(data.getScode())) {
                        dates.add(data.getSdate());
                    } else {
                        map.put("0" + "." + oldkey, dates);
                        dates = new ArrayList<String>();
                        dates.add(data.getSdate());
                        oldkey = data.getScode();
                    }

                }
                if (null != dates && dates.size() > 0) {
                    map.put("0" + "." + oldkey, dates);
                }
            }
        for (Integer key: map1.keySet()) {
            List<String> codes = map1.get(key);
            lambda.clear();
            lambda.select(MkIndivDay::getScode, MkIndivDay::getSdate);
            lambda.eq(MkIndivDay::getSmarket, 1);
            lambda.in(MkIndivDay::getScode, codes);
            List<MkIndivDay> datelist = list(lambda);
            List dates = new ArrayList<String>();
            String oldkey = "";
            for (MkIndivDay data : datelist) {
                if (oldkey.equals(data.getScode())) {
                    dates.add(data.getSdate());
                } else {
                    map.put("0" + "." + oldkey, dates);
                    dates = new ArrayList<String>();
                    dates.add(data.getSdate());
                    oldkey = data.getScode();
                }
            }
            if (null != dates && dates.size() > 0) {
                map.put("0" + "." + oldkey, dates);
            }
        }
            System.out.println("Stcok自带的线程池结束" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
            return new AsyncResult<>(map);
    }

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
                mkIndivDay.setDifferrange(stock.getBigDecimal("f3", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //涨跌额
                mkIndivDay.setDiffer(stock.getBigDecimal("f4", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //成交量(手)
                mkIndivDay.setVolume(stock.getInt("f5", 0));
                //成交额
                mkIndivDay.setAmount(stock.getBigDecimal("f6", BigDecimal.ZERO));
                //振幅%
                mkIndivDay.setAmplitude(stock.getBigDecimal("f7", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //换手率%
                mkIndivDay.setTurn(stock.getBigDecimal("f8", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //市盈率(动态)
                mkIndivDay.setPe(stock.getBigDecimal("f9", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //量比
                mkIndivDay.setVolratio(stock.getBigDecimal("f10", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //分表代码
                String scode=stock.getStr("f12", null);
                mkIndivDay.setScodeNum(null == scode? 0: Integer.valueOf(scode));
                //代码
                mkIndivDay.setScode(scode);
                //市场
                mkIndivDay.setSmarket(stock.getInt("f13", null));
                //名称
                mkIndivDay.setSname(stock.getStr("f14", null));
                //最高
                mkIndivDay.setHigh(stock.getBigDecimal("f15", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //最低
                mkIndivDay.setLow(stock.getBigDecimal("f16", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //开盘
                mkIndivDay.setOpen(stock.getBigDecimal("f17", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //昨收
                mkIndivDay.setPreclose(stock.getBigDecimal("f18", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //总市值
                mkIndivDay.setMv(stock.getBigDecimal("f20", BigDecimal.ZERO));
                //流通市值
                mkIndivDay.setLiqmv(stock.getBigDecimal("f21", BigDecimal.ZERO));
                //市净
                mkIndivDay.setPb(stock.getBigDecimal("f23", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));
                //今日主力净流入
                mkIndivDay.setNetinflow(stock.getBigDecimal("f62", BigDecimal.ZERO));
                //TTM市盈率
                mkIndivDay.setPettm(stock.getBigDecimal("f115", BigDecimal.ZERO).divide(BigDecimal.valueOf(100)));

                list.add(mkIndivDay);
            }
            if (list != null && list.size() > 0) {
                saveBatch(list);
            }
        } finally {
            countDownLatch.countDown();
        }
    }



    /**
     * 日期	收盘	涨跌幅(%)	涨跌额	成交量(手)	成交额	振幅%	换手率%	市盈率(动态)	量比	代码	市场	名称	最高	最低	开盘	昨收	总市值	流通市值	市净	今日主力净流入	TTM市盈率(动态)
     * 	CLOSE	DIFFERRANGE	DIFFER	VOLUME	AMOUNT	AMPLITUDE	TURN	PE	VOLRATIO	SCODE	SCLASS	SNAME	HIGH	LOW	OPEN	PRECLOSE	MV	LIQMV	PB	NETINFLOW	PETTM
     * klines：	klines：	klines：	klines：	klines：	klines：	klines：	klines：			code	market	name	klines：	klines：	klines：
     * 0	2	8	9	5	6	7	10						3	4	1
     * 解析爬取到的个股信息K线图 --东方财富网
     * @param content
     */
    @Async("myAsync")
    public Future<List<MkIndivDay>> analysisPast(String content, List dates) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Stcok自带的线程池开始" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));

        if (content == null) return null;

        List list = new ArrayList<MkIndivDay>();

        JSONObject jsonObject = JSONUtil.parseObj(content);
        JSONObject data = jsonObject.getJSONObject("data");
        if (null == data) return null;
        //代码
        String scode = data.getStr("code");
        //市场
        int smarket = data.getInt("market");
        //名称
        String sname = data.getStr("name");

        List<String> klines = data.getJSONArray("klines").toList(String.class);

        for (String kline : klines) {
            kline.replaceAll("\"", "");
            String[] split = kline.split(",");
            //日期
            String sdate = split[0].replace("-", "");
            //已经存在日期，不保存
            if (dates.contains(sdate)) continue;

           MkIndivDay mkIndivDay = new MkIndivDay();

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
            //分表代码
            mkIndivDay.setScodeNum(null == scode ? 0 : Integer.valueOf(scode));
            //代码
            mkIndivDay.setScode(scode);
            //市场
            mkIndivDay.setSmarket(smarket);
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
        System.out.println("Stcok自带的线程池结束" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
        return new AsyncResult<>(list);
    }


}
