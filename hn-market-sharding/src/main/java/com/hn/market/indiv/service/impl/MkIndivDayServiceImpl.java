package com.hn.market.indiv.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hn.market.common.utils.FileIOUtils;
import com.hn.market.common.utils.MyDateUtils;
import com.hn.market.common.utils.WebCrawlerUtils;
import com.hn.market.indiv.service.MkIndivDayService;
import com.hn.market.indiv.thread.MkIndivDayThread;
import com.hn.market.mbg.mapper.MkIndivDayMapper;
import com.hn.market.mbg.model.MkIndivDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    @Value("${df.market.indiv.details.path}")
    private String DF_MARKET_INDIV_DETAILS_PATH;
    @Value("${df.market.indiv.details.url}")
    private String DF_MARKET_INDIV_DETAILS_URL;


    @Autowired
    private WebCrawlerUtils webCrawlerUtils;

    @Autowired
    private MyDateUtils myDateUtils;

    @Autowired
    private MkIndivDayThread mkIndivDayThread;

    @Autowired
    private FileIOUtils fileIOUtils;

    /**
     * 查询最新个股行情（包含沪深京A股） ，如果数据库没有数据，网盘爬取
     *
     * @param smarket
     * @param pageSize
     * @param pageNum
     * @return 涨跌幅，市场，代码 降序排列
     */
    @Override
    public List<MkIndivDay> list(String smarket, String scode, String sname, String sdate, Integer pageSize, Integer pageNum) {

        //返回数据
        Page<MkIndivDay> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
        //判断是否已经爬取,根据最新日期
        String ndate = myDateUtils.getWorkDate();
        if (StrUtil.isNotEmpty(sdate) && !sdate.equals(ndate)) {
            lambda.eq(MkIndivDay::getSdate, sdate);
        } else {
            lambda.eq(MkIndivDay::getSdate, ndate);
            baseMapper.delete(lambda);
            //爬取数据
            String content = webCrawlerUtils.getCrawler(DF_MARKET_INDIV_URL);
            //解析数据
            List<MkIndivDay> list = analysis(content, ndate);
            saveBatch(list);
        }

        if (StrUtil.isNotEmpty(smarket)) {
            lambda.eq(MkIndivDay::getSmarket, smarket);
        }
        if (StrUtil.isNotEmpty(scode)) {
            lambda.eq(MkIndivDay::getScode, scode);
        }
        if (StrUtil.isNotEmpty(sname)) {
            lambda.like(MkIndivDay::getSname, sname);
        }

        lambda.orderByDesc(MkIndivDay::getDifferrange, MkIndivDay::getSmarket, MkIndivDay::getScode);
        return list(lambda);
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
    public List<MkIndivDay> listPast(String scode, String sname, Integer pageSize, Integer pageNum) {
        //返回数据
        Page<MkIndivDay> page = new Page<>(pageNum, pageSize);
        //爬虫路径
        String url = DF_MARKET_INDIV_PAST + DF_SCLASS_SCODE;

        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
        lambda.select(MkIndivDay::getScode, MkIndivDay::getSmarket, MkIndivDay::getSdate);

        String sdate = myDateUtils.getWorkDate();
        lambda.eq(MkIndivDay::getSdate, sdate);

        if (StrUtil.isNotEmpty(scode)) {
            lambda.eq(MkIndivDay::getScode, scode);
        }
        if (StrUtil.isNotEmpty(sname)) {
            lambda.like(MkIndivDay::getSname, sname);
        }

        List<MkIndivDay> lists = list(lambda);

        //list去重
        // list.stream().filter(distinctByKey(MkIndivDay::getScode)).collect(Collectors.toList());

        long start = Calendar.getInstance().getTimeInMillis();
        //查看股票数据的日期；
        Map<String, List> map = new HashMap<>();
        String newScode = "";
        // 异步多线程  插入数据库
        int total = lists.size();
        //1千条数据开一条线程
        int batchSize = 100;
        int number = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            List list = new ArrayList<JSONObject>();
            if (i == number - 1) {
                list = lists.subList(i * batchSize, total);
            } else {
                list = lists.subList(i * batchSize, (i + 1) * batchSize);
            }
            Future<Map<String, List>> future = mkIndivDayThread.getScodeSdate(list);
            futures.add(future);
        }

        for (Future<Map<String, List>> future : futures) {
            try {
                map.putAll(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("fun1==========>" + ((Calendar.getInstance().getTimeInMillis() - start) / 1000));

        start = Calendar.getInstance().getTimeInMillis();
        int i = 0;
        for (String key : map.keySet()) {
            String urlPath = url + key;
            String content = webCrawlerUtils.getCrawler(urlPath);
            // 解析Json
            mkIndivDayThread.analysisPast(content, map.get(key));
            try {
                i++;
                if(i%10==0) {
                    Thread.sleep(1000);
                    System.out.println("已处理条数"+i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("fun2==========>" + ((Calendar.getInstance().getTimeInMillis() - start) / 1000));
        lambda.clear();
        lambda.last(" limit 5 ");
        return list(lambda);
    }

    @Override
    public boolean delete(String scode, String sname, String sdate) {
        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(scode)) {
            lambda.eq(MkIndivDay::getScode, scode);
        }
        if (StrUtil.isNotEmpty(sname)) {
            lambda.like(MkIndivDay::getSname, sname);
        }
        if (StrUtil.isNotEmpty(sdate)) {
            lambda.eq(MkIndivDay::getSdate, sdate);
        }
        int delete = baseMapper.delete(lambda);
        if (delete > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean createDetails(String scode, String sname, String ndays) {
        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
        lambda.select(MkIndivDay::getSmarket,MkIndivDay::getScode);
        if (StrUtil.isNotEmpty(scode)) {
            lambda.eq(MkIndivDay::getScode, scode);
        }
        if (StrUtil.isNotEmpty(sname)) {
            lambda.like(MkIndivDay::getSname, sname);
        }
        //根据最新日期
        String ndate = myDateUtils.getWorkDate();
        lambda.like(MkIndivDay::getSdate, ndate);
        List<MkIndivDay> list = list(lambda);
        String url = DF_MARKET_INDIV_DETAILS_URL+ndays+DF_SCLASS_SCODE;
        String filePath = DF_MARKET_INDIV_DETAILS_PATH+myDateUtils.getNewDate().substring(0,6);
        for (MkIndivDay mk:list){
            String secid = mk.getSmarket()+"."+mk.getScode();
            String httpUrl = url +secid;
            String content = webCrawlerUtils.getCrawler(httpUrl);
            try {
                String  newfilepath = fileIOUtils.creatTxtFile(filePath, mk.getScode());
                mkIndivDayThread.analysisDetails(content,newfilepath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    /**
     * json：	f1:2,	f2:34.86,	f3:20.0,	f4:5.81,	f5:76938,	f6:257140310.76,	f7:11.74,	f8:19.71,	f9:36.51,	f10:3.83,	f11:0.0,	f12:"300926",	f13:0,	f14:"博俊科技",	f15:34.86,	f16:31.45,	f17:31.61,	f18:29.05,	f20:5399998758,	f21:1360704324,	f22:0.0,	f23:4.24,	f24:66.87,	f25:67.35,	f62:32428363.0,	f115:36.51,	f128:"-",	f140:"-",	f141:"-",	f136:"-",	f152:2
     * 数据库：		收盘	涨跌幅(%)	涨跌额	成交量(手)	成交额	振幅%	换手率%	市盈率(动态)	量比		代码	市场	名称	最高	最低	开盘	昨收	总市值	流通市值		市净			今日主力净流入	TTM市盈率(动态)
     * CLOSE	DIFFERRANGE	DIFFER	VOLUME	AMOUNT	AMPLITUDE	TURN	PE	VOLRATIO		SCODE	SCLASS	SNAME	HIGH	LOW	OPEN	PRECLOSE	MV	LIQMV		PB			NETINFLOW	PETTM
     * 解析爬取到的个股信息 --东方财富网
     *
     * @param content
     * @return
     */
    public List<MkIndivDay> analysis(String content, String sdate) {

        if (content == null) return null;

        JSONObject jsonObject = JSONUtil.parseObj(content);
        JSONObject diff = jsonObject.getJSONObject("data").getJSONObject("diff");

        List diffLists = new ArrayList<JSONObject>();
        for (int i = 0; i < diff.size(); i++) {
            JSONObject stock = diff.getJSONObject(String.valueOf(i));
            diffLists.add(stock);
        }
        List list = insertMk(diffLists, sdate);
        return list;
    }

    public List<MkIndivDay> insertMk(List<JSONObject> diffList, String sdate) {
        List list = new ArrayList<MkIndivDay>();
        for (JSONObject stock : diffList) {
            //代码
            String scode = stock.getStr("f12", null);

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
            mkIndivDay.setScodeNum(null == scode ? 0 : Integer.valueOf(scode));
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
        return list;
    }


}
