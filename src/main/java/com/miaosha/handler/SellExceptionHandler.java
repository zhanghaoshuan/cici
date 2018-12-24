package com.miaosha.handler;


import com.miaosha.exception.AuthorizeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 廖师兄
 * 2017-07-30 17:44
 */
//@Component
//public class SellExceptionHandler {
//
//
//    //拦截登录异常
//    //http://sell.natapp4.cc/sell/wechat/qrAuthorize?returnUrl=http://sell.natapp4.cc/sell/seller/login
//    @ExceptionHandler(value = AuthorizeException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ModelAndView handlerAuthorizeException() {
//        return new ModelAndView("redirect:"
//                .concat("/login/do_login"));
//    }
//
//
//}
