package com.hn.market.indiv.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hn.market.common.utils.MyDateUtils;
import com.hn.market.common.utils.WebCrawlerUtils;
import com.hn.market.indiv.service.MkIndivDayService;
import com.hn.market.indiv.thread.MkIndivDayThread;
import com.hn.market.mbg.mapper.MkIndivDayMapper;
import com.hn.market.mbg.model.MkIndivDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>
 * 日行情 服务实现类
 * </p>
 *
 * @author macro
 * @since 2023-04-04
 */
@Service
public class MkIndivDayServiceImpl extends ServiceImpl<MkIndivDayMapper, MkIndivDay> implements MkIndivDayService {

    @Value("${df.market.indiv}")
    private String DF_MARKET_INDIV_URL;
    @Value("${df.market.indiv.past}")
    private String DF_MARKET_INDIV_PAST;
    @Value("${df.sclass.scode}")
    private String DF_SCLASS_SCODE;

    @Autowired
    private WebCrawlerUtils webCrawlerUtils;

    @Autowired
    private MyDateUtils myDateUtils;

    @Autowired
    private MkIndivDayThread mkIndivDayThread;

    public CountDownLatch countDownLatch = null;

    /**
     * 查询最新个股行情（包含沪深京A股） ，如果数据库没有数据，网盘爬取
     *
     * @param sclass
     * @param pageSize
     * @param pageNum
     * @return 涨跌幅，市场，代码 降序排列
     */
    @Override
    public Page<MkIndivDay> list(String sclass, Integer pageSize, Integer pageNum) {

        //返回数据
        Page<MkIndivDay> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();

        //判断是否已经爬取,根据最新日期
        String sdate = myDateUtils.getWorkDate();
        lambda.eq(MkIndivDay::getSdate, sdate);
        if (count(lambda)<=0) {
            //爬取数据
            String content = webCrawlerUtils.getCrawler(DF_MARKET_INDIV_URL);
            //解析数据
            analysis(content, sdate);
        }

        //返回数据
        if (StrUtil.isNotEmpty(sclass)) {
            lambda.eq(MkIndivDay::getSclass, sclass);
        }
        lambda.orderByDesc(MkIndivDay::getDifferrange, MkIndivDay::getSclass, MkIndivDay::getScode);
        return page(page, wrapper);
    }

    /**
     * json			数据库
     * f1:2,
     * f2:34.86,			收盘	CLOSE
     * f3:20.0,			涨跌幅(%)	DIFFERRANGE
     * f4:5.81,			涨跌额	DIFFER
     * f5:76938,			成交量(手)	VOLUME
     * f6:257140310.76,			成交额	AMOUNT
     * f7:11.74,			振幅%	AMPLITUDE
     * f8:19.71,			换手率%	TURN
     * f9:36.51,			市盈率(动态)	PE
     * f10:3.83,			量比	VOLRATIO
     * f11:0.0,
     * f12:"300926",			代码	SCODE
     * f13:0,			市场	SCLASS
     * f14:"博俊科技",			名称	SNAME
     * f15:34.86,			最高	HIGH
     * f16:31.45,			最低	LOW
     * f17:31.61,			开盘	OPEN
     * f18:29.05,			昨收	PRECLOSE
     * f20:5399998758,			总市值	MV
     * f21:1360704324,			流通市值	LIQMV
     * f22:0.0,
     * f23:4.24,			市净	PB
     * f24:66.87,
     * f25:67.35,
     * f62:32428363.0,			今日主力净流入	NETINFLOW
     * f115:36.51,			TTM市盈率(动态)	PETTM
     * f128:"-",
     * f140:"-",
     * f141:"-",
     * f136:"-",
     * f152:2
     * 解析爬取到的个股信息 --东方财富网
     *
     * @param content
     * @return
     */
    public void analysis(String content, String sdate) {

        if (content == null) return;

        JSONObject jsonObject = JSONUtil.parseObj(content);
        JSONObject diff = jsonObject.getJSONObject("data").getJSONObject("diff");

        List diffLists = new ArrayList<JSONObject>();
        for (int i = 0; i < diff.size(); i++) {
            JSONObject stock = diff.getJSONObject(String.valueOf(i));
            diffLists.add(stock);
        }

        // 异步多线程  插入数据库
        int total = diffLists.size();
        //两千条数据开一条线程
        int batchSize = 1000;
        int number = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
        countDownLatch = new CountDownLatch(number);

        for (int i = 0; i < number; i++) {
            List diffList = new ArrayList<JSONObject>();
            if (i == number - 1) {
                diffList = diffLists.subList(i * batchSize, total);
            } else {
                diffList = diffLists.subList(i * batchSize, (i + 1) * batchSize);
            }
            mkIndivDayThread.insertMk(diffList,sdate , countDownLatch);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询上市以来所有个股行情（包含沪深京A股） ，如果数据库没有数据，网盘爬取；
     *
     * @param scode
     * @param sname
     * @param pageSize
     * @param pageNum
     * @return 代码 日期
     */
    @Override
    public Page<MkIndivDay> listPast(String scode, String sname, Integer pageSize, Integer pageNum) {
        //返回数据
        Page<MkIndivDay> page = new Page<>(pageNum, pageSize);
        //爬虫路径
        String url = DF_MARKET_INDIV_PAST + DF_SCLASS_SCODE;

        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
        // lambda.select(MkIndivDay::getScode, MkIndivDay::getSclass, MkIndivDay::getSdate);
        if (StrUtil.isNotEmpty(scode)) {
            lambda.eq(MkIndivDay::getScode, scode);
        }
        if (StrUtil.isNotEmpty(sname)) {
            lambda.like(MkIndivDay::getSname, sname);
        }
        lambda.orderByDesc(MkIndivDay::getScode, MkIndivDay::getSdate);
        List<MkIndivDay> list = list(lambda);

        //list去重
        // list.stream().filter(distinctByKey(MkIndivDay::getScode)).collect(Collectors.toList());
        //查看股票数据的日期；
        HashMap<String, List> map = new HashMap<>();
        String oldScode = "";
        String newScode = "";
        List dates = new ArrayList<String>();
        for (MkIndivDay mk : list) {
            newScode = mk.getSclass() + "." + mk.getScode();
            if (newScode.equals(oldScode)) {
                dates.add(mk.getSdate());
            } else {
                if (null != dates && dates.size() > 0) {
                    map.put(newScode, dates);
                }
                dates = new ArrayList<String>();
                dates.add(mk.getSdate());
                oldScode = newScode;
            }
        }
        if (null != dates && dates.size() > 0) {
            map.put(newScode, dates);
        }

        for (String key : map.keySet()) {
            String urlPath = url + key;
            String content = webCrawlerUtils.getCrawler(urlPath);
            // 解析Json
            analysisPast(content, map.get(key));
        }
        return page(page, wrapper);
    }

    /**
     * K线图
     * 日期		klines：	0
     * <p>
     * 收盘	CLOSE	klines：	2
     * 涨跌幅(%)	DIFFERRANGE	klines：	8
     * 涨跌额	DIFFER	klines：	9
     * 成交量(手)	VOLUME	klines：	5
     * 成交额	AMOUNT	klines：	6
     * 振幅%	AMPLITUDE	klines：	7
     * 换手率%	TURN	klines：	10
     * 市盈率(动态)	PE
     * 量比	VOLRATIO
     * <p>
     * 代码	SCODE	code
     * 市场	SCLASS	market
     * 名称	SNAME	name
     * 最高	HIGH		3
     * 最低	LOW		4
     * 开盘	OPEN		1
     * 昨收	PRECLOSE
     * 总市值	MV
     * 流通市值	LIQMV
     * <p>
     * 市净	PB
     * <p>
     * <p>
     * 今日主力净流入	NETINFLOW
     * TTM市盈率(动态)	PETTM
     * <p>
     * 解析爬取到的个股信息K线图 --东方财富网
     * ！ ！
     *
     * @param content
     */
    public void analysisPast(String content, List dates) {

        if (content == null) return;

        MkIndivDay MkIndivDay = new MkIndivDay();
        List<MkIndivDay> list = new ArrayList<MkIndivDay>();

        JSONObject jsonObject = JSONUtil.parseObj(content);
        JSONObject data = jsonObject.getJSONObject("data");
        if (null == data) return;
        //代码
        String scode = data.getStr("code");
        //市场
        String smarket = data.getStr("market");
        //名称
        String sname = data.getStr("name");

        List<String> klines = data.getJSONArray("klines").toList(String.class);

        // 异步多线程  插入数据库
        int total = klines.size();
        //两千条数据开一条线程
        int batchSize = 3000;
        int number = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
        CountDownLatch countDownLatch = new CountDownLatch(number);

        for (int i = 0; i < number; i++) {
            List<String> klineList = new ArrayList<>();
            if (i == number - 1) {
                klineList = klines.subList(i * batchSize, total);
            } else {
                klineList = klines.subList(i * batchSize, (i + 1) * batchSize);
            }
            mkIndivDayThread.insertMkPast(klineList, dates, scode, smarket, sname, countDownLatch);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取list中对象属性的重复值
     */
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
