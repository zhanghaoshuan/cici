package com.miaosha.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Administrator on 2018/11/8.
 */
@Slf4j
public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static Date StringToDate(String str){
        SimpleDateFormat sdf=new SimpleDateFormat(STANDARD_FORMAT);
        Date date=null;
        try {
            date=sdf.parse(str);
        } catch (ParseException e) {
            log.error("日期装换失败");
        }
        return date;
    }

    public static String DateToStr(Date date){
        if(date==null){
            return "";
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(STANDARD_FORMAT);
        return simpleDateFormat.format(date);
    }
}
