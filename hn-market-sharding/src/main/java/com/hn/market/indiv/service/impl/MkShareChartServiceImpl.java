package com.hn.market.indiv.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hn.market.entity.indiv.mapper.MkShareChartMapper;
import com.hn.market.entity.indiv.model.MkShareChart;
import com.hn.market.indiv.service.MkShareChartService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 分时图 服务实现类
 * </p>
 *
 * @author MyBatisPlus代码生成器
 * @since 2023-04-27
 */
@Service
public class MkShareChartServiceImpl extends ServiceImpl<MkShareChartMapper, MkShareChart> implements MkShareChartService {

    @Value("${df.market.indiv.details.path}")
    private String DF_MARKET_INDIV_DETAILS_PATH;

    /**
     * #f51	f53	f56	f57	f58	f59
     * #时间	交易价格	现手	成交额	均价	委卖
     * @param scode
     * @param sname
     * @param ndate
     * @param sday
     * @return
     */
    @Override
    public List<MkShareChart> list(String scode, String sname, String ndate, int sday) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if(StringUtils.isEmpty(ndate)){
            ndate=sdf.format(new Date());
        }
        String start_date = null;
        String end_date = ndate;
        String filePath = null;
        String filePath_per = null;
        try {
            Date s_date = DateUtils.parseDate(ndate, "yyyyMMdd");
            start_date = sdf.format(DateUtils.addDays(s_date, -(sday-1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        filePath = DF_MARKET_INDIV_DETAILS_PATH + ndate.substring(0, 6);
        //查询符合日期的数据
        List<String> datas = getDateOfCompliance(filePath, scode, start_date, end_date);
        if (!ndate.substring(0, 6).equals(start_date.substring(0, 6))) {
            filePath_per = DF_MARKET_INDIV_DETAILS_PATH + start_date.substring(0, 6);
            List<String> datas_pre = getDateOfCompliance(filePath_per, scode, start_date, end_date);
            datas.addAll(datas_pre);
        }
        //解析符合日期的数据
        List<MkShareChart> list = analysisData(datas);

        return list;
    }

    private List<String> getDateOfCompliance(String filePath, String scode,String start_date,String end_date){

        List datas = new ArrayList<String>();

        File file = new File(filePath, scode+".txt");
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String str = null;
            while ((str = reader.readLine()) != null) {
                JSONObject jsonObject = JSONUtil.parseObj(str);
                String sdate = jsonObject.get("sdate").toString();
                String edate = jsonObject.get("edate").toString();
                if(sdate.compareTo(end_date)>0 || edate.compareTo(start_date)<0){
                    continue;
                }
                JSONArray list = jsonObject.getJSONObject("data").getJSONArray("trends");
                if (list!=null&&list.size()>0) {
                    for (int i = 0; i < list.size(); i++) {
                        String data= list.get(i).toString();
                        String[] split = data.split(",");
                        if (split.length == 6) {
                            String date = split[0].substring(0,10).replace("-", "");
                            if(date.compareTo(start_date)>=0 && date.compareTo(end_date)<=0){
                                datas.add(data);
                            }
                        }
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datas;
    }

    private List<MkShareChart> analysisData(List<String> list) {
        if(list==null) return null;
        MkShareChart vo = new MkShareChart();
        ArrayList<MkShareChart> vos = new ArrayList<>();
        for (String str : list) {
            String[] split = str.split(",");
            if (split.length == 6) {
                vo = new MkShareChart();
                vo.setStime(split[0]);
                vo.setPrice(new BigDecimal(split[1]));
                vo.setNum(Integer.valueOf(split[2]));
                vo.setAmount(new BigDecimal(split[3]));
                vo.setAvgPrice(new BigDecimal(split[4]));
                vo.setCommissionBuyNum(Integer.valueOf(split[5]));
                vos.add(vo);
            }
        }
        return vos;
    }


}
