package com.miaosha.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018/11/8.
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper=new ObjectMapper();
    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //所有的日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }
    public static <T> String obj2Str(T obj){
        if(obj==null){
            return "";
        }
        try {
            return obj instanceof String?(String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error("无法将obj转化成str");
            return null;
        }
    }

    public static <T> T str2Obj(String str,Class<T> clazz){
        if(StringUtils.isBlank(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)? (T)str:objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.error("无法将str转化成obj");
            return null;
        }
    }
    public static <T> T  str2Obj(String str,TypeReference<T> tTypeReference ){
        if(StringUtils.isBlank(str)||tTypeReference==null){
            return null;
        }
        try {
            return tTypeReference.getType().equals(String.class)? (T)str:objectMapper.readValue(str,tTypeReference);
        } catch (IOException e) {
            log.error("无法将str转化成obj");
            return null;
        }
    }

    public static <T> T str2Obj(String str,Class<?> collectionClass,Class<?>... elementClass){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to object error",e);
            return null;
        }
    }
}
