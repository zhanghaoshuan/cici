package com.miaosha.util;

import com.miaosha.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018/11/7.
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN="localhost";
    private final static String COOKIE_NAME="token";
    public static String getCookieToken(HttpServletRequest httpServletRequest){

        Cookie[] cookies= httpServletRequest.getCookies();
        if(cookies==null){
            log.error("该用户完全没有cookie");
            return null;
        }
        for(Cookie cookie:cookies){
            if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                return  cookie.getValue();
            }
        }
        return null;
    }
    public static void writeloginToken(HttpServletResponse response, String token){
        Cookie cookie=new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");//代表设置在根目录
        cookie.setHttpOnly(true);//防止脚本攻击带来的信息泄露风险  可以让浏览器不把cookie发送给第三方，
        cookie.setMaxAge(-1);//如果是-1代表没有期限 ，cookie永久有效
        log.info("write cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }
    public static void delLoginToken(HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
        Cookie[] cookies=httpServletRequest.getCookies();
        if(cookies==null){
            log.error("该用户完全没有cookie");
            return ;
        }
        for(Cookie cookie:cookies){
            if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                cookie.setMaxAge(0);
                cookie.setDomain(COOKIE_DOMAIN);
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                return;
            }
        }
        return ;
    }
}
