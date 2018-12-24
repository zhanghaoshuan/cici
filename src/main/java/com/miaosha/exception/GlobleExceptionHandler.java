package com.miaosha.exception;

import com.miaosha.common.ServerResponse;
import com.miaosha.enums.CodeMsg;
import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/11/9.
 */
@ControllerAdvice
@Slf4j
public class GlobleExceptionHandler {

    @ExceptionHandler(value = AuthorizeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handlerAuthorizeException() {
        return  "login";
    }

    @ExceptionHandler(value=Exception.class)
    @ResponseBody
    public ServerResponse exceptionHandler(HttpServletRequest httpServletRequest,Exception e){
        log.error("{}Exception",httpServletRequest.getRequestURI(),e);
        ServerResponse result=new ServerResponse(CodeMsg.FAIL.getCode(),"你的操作有误",null);
        return result;
    }
}
