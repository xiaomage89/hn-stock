package com.hn.market.common.utils;


import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author majj
 * @create 2023-04-02 22:26
 */
@Component
public class MyDateUtils {


    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    /**
     * 获取股市开盘最新时日期
     * 除去周末和节假日
     */
    public String getWorkDate(){

        Calendar cal = Calendar.getInstance();
        int dayweek=cal.get(Calendar.DAY_OF_WEEK);
        //周六 7 --  周日 1
        if(7==dayweek){
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }else if(1==dayweek){
            cal.add(Calendar.DAY_OF_MONTH,-2);
        }
        String sdate = sdf.format(cal.getTime());

        return sdate;
    }

    /**
     * 获取当前年月日（yyyyMMdd）
     * @return
     */
    public String getNewDate(){
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());
    }

}
