package com.miaosha.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.miaosha.enums.CodeMsg;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/11/7.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
public class ServerResponse<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public ServerResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    @JsonIgnore
    public boolean isSuccess(){
        return this.code== CodeMsg.FAIL.getCode();
    }
    public static <T> ServerResponse<T> createBySuccessData(T data){
        return new ServerResponse<>(CodeMsg.SUCCESS.getCode(),null,data);
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<>(CodeMsg.SUCCESS.getCode(),msg,null);
    }
    public static <T> ServerResponse<T> createBySuccess(String desc,T data){
        return new ServerResponse<T>(CodeMsg.SUCCESS.getCode(),desc,data);
    }
    public static <T> ServerResponse<T> createByError(String desc,T data){
        return new ServerResponse<T>(CodeMsg.FAIL.getCode(),desc,data);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int code,String desc){
        return new ServerResponse<T>(code,desc,null);
    }
    public static <T> ServerResponse<T> createByErrorMessage(String desc) {
        return new ServerResponse<T>(CodeMsg.FAIL.getCode(), desc, null);
    }
    public static <T> ServerResponse<T> createByErrorCodeMsg(CodeMsg codeMsg){
        return new ServerResponse<>(codeMsg.getCode(),codeMsg.getMsg(),null);
    }
}
