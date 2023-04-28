package com.hn.market.indiv.thread;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hn.market.entity.indiv.mapper.MkIndivDayMapper;
import com.hn.market.entity.indiv.model.MkIndivDay;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.*;
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
    public Future<Map<String, List>> getScodeSdate(List<MkIndivDay> list) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Stcok自带的线程池开始" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
        //查看股票数据的日期；
        Map<String, List> map = new HashMap<>();
        QueryWrapper<MkIndivDay> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<MkIndivDay> lambda = wrapper.lambda();
        for (MkIndivDay mk : list) {
            String key = mk.getSmarket() + "." + mk.getScode();
            lambda.clear();
            lambda.select(MkIndivDay::getSdate);
            lambda.eq(MkIndivDay::getSmarket, mk.getSmarket());
            lambda.eq(MkIndivDay::getScode, mk.getScode());
            List<MkIndivDay> datelist = list(lambda);
            List dates = new ArrayList<String>();
            for (MkIndivDay date : datelist) {
                dates.add(date.getSdate());
            }
            map.put(key, dates);
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
                String scode = stock.getStr("f12", null);
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
     * CLOSE	DIFFERRANGE	DIFFER	VOLUME	AMOUNT	AMPLITUDE	TURN	PE	VOLRATIO	SCODE	SCLASS	SNAME	HIGH	LOW	OPEN	PRECLOSE	MV	LIQMV	PB	NETINFLOW	PETTM
     * klines：	klines：	klines：	klines：	klines：	klines：	klines：	klines：			code	market	name	klines：	klines：	klines：
     * 0	2	8	9	5	6	7	10						3	4	1
     * 解析爬取到的个股信息K线图 --东方财富网
     *
     * @param content
     */
    @Async("myAsync")
    public Future<String> analysisPast(String content, List dates) {

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
        saveBatch(list);
        return new AsyncResult<>("本线程结束");
    }


    @Async("myAsync")
    public void analysisDetails(String content, String filepath) {

        try {
            //取文件最后一行的第一个日期，判读是否重复写入
            // HashMap<String, Integer> dateMap = new HashMap<String, Integer>();
            // String lineStr = getLastLineStr(filepath,null);
            // JSONObject jsonObject = JSONUtil.parseObj(lineStr);
            // JSONArray list = jsonObject.getJSONObject("data").getJSONArray("trends");
            // if (list.size()>0) {
            //     String date = list.getStr(0).substring(0, 10);
            //     if (content.contains(date)) {
            //         return;
            //     }
            // }

            //添加开始日期和结束日期
            String sDate="";
            String eDate="";
            String nDate="";
            JSONObject jsonObject = JSONUtil.parseObj(content);
            JSONArray list = jsonObject.getJSONObject("data").getJSONArray("trends");
            if (list!=null&&list.size()>0) {
                String[] datas = list.getStr(0).split("\",\"");
                for (int i = 0; i < list.size(); i++) {
                    String data= list.get(i).toString();
                    String[] split = data.split(",");
                    if (split.length == 6) {
                        nDate = split[0].substring(0,10).replace("-", "");
                        if(i==0){
                            sDate = nDate;
                            eDate = nDate;
                        }
                        if (nDate.compareTo(sDate) < 0) {
                            sDate = nDate;
                        } else if (nDate.compareTo(eDate) > 0) {
                            eDate = nDate;
                        } else {
                            continue;
                        }
                    }
                }
            }
            jsonObject.putOpt("sdate",sDate);
            jsonObject.putOpt("edate",eDate);
            content=jsonObject.toString();
            FileWriter fw = new FileWriter(filepath, true);
            fw.write(content + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文本文件最后一行中的字符串。 * @param file * 目标文件 * @param charset * 字符编码 * @return 文本文件中最后一行中的字符串。
     */
    public  String getLastLineStr(String filepath, String charset) {
        String lastLine = null;
        RandomAccessFile raf = null;
        try {
            File file = new File(filepath);
            raf = new RandomAccessFile(file, "rwd");
            //获取最后一行的起始位置，并移动指针到指定位置。
            long lastLinePos = getLastLinePos(raf);
            //获取文件的大小：也就是占用的字节数
            long length = raf.length();
            byte[] bytes = new byte[(int) ((length - 1) - lastLinePos)];
            //上面的getLastLinePos(raf);方法已经移动文件指针到最后一行的起始位置了，所以这里只需要读取即可。
            raf.read(bytes);
            if (charset == null) {
                lastLine = (new String(bytes));
            } else {
                lastLine = (new String(bytes, charset));
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lastLine;
    }

    /**
     * 返回最后一行的起始位置,并移动文件指针到最后一行的起始位置。 * @param raf RandomAccessFile对象 * @return 最后一行的起始位置 * @throws IOException
     */
    private long getLastLinePos(RandomAccessFile raf) throws IOException {
        long lastLinePos = 0L;
        // 获取文件占用字节数
        long len = raf.length();
        if (len > 0L) {
            // 向前走一个字节
            long pos = len - 1;
            while (pos > 0) {
                pos--;
                // 移动指针
                raf.seek(pos);
                // 判断这个字节是不是回车符
                if (raf.readByte() == '\n') {
                    // lastLinePos = pos;// 记录下位置
                    // break;// 前移到会第一个回车符后结束
                    return pos;
                }
            }
        }
        return lastLinePos;
    }

    /*
        分时图数据 补充开始时间和结束时间
     */
    @Async("myAsync")
    public void addDate(List<File> files){
        for(File file : files){
            try {
                String path = file.getPath();
                String newPath = path.replaceAll(".txt","")+"_a.txt";
                new File(newPath).createNewFile();
                FileWriter fw = new FileWriter(newPath, true);

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String content=null;
                while((content=reader.readLine())!=null){
                    //添加开始日期和结束日期
                    String sDate="";
                    String eDate="";
                    String nDate="";
                    System.out.println(content);
                    JSONObject jsonObject = JSONUtil.parseObj(content);
                    JSONArray list = jsonObject.getJSONObject("data").getJSONArray("trends");
                    if (list!=null&&list.size()>0) {
                        String[] datas = list.getStr(0).split("\",\"");
                        for (int i = 0; i < list.size(); i++) {
                            String data= list.get(i).toString();
                            String[] split = data.split(",");
                            if (split.length == 6) {
                                nDate = split[0].substring(0,10).replace("-", "");
                                if(i==0){
                                    sDate = nDate;
                                    eDate = nDate;
                                }
                                if (nDate.compareTo(sDate) < 0) {
                                    sDate = nDate;
                                } else if (nDate.compareTo(eDate) > 0) {
                                    eDate = nDate;
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                    jsonObject.putOpt("sdate",sDate);
                    jsonObject.putOpt("edate",eDate);
                    content=jsonObject.toString();

                    fw.write(content + "\n");
                }
                reader.close();
                fw.close();
                file.delete();
                new File(newPath).renameTo(new File(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
