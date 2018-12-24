package com.miaosha.controller;

import com.miaosha.common.ServerResponse;
import com.miaosha.constant.RedisConstant;
import com.miaosha.redis.MiaoshaUserKey;
import com.miaosha.service.UserService;
import com.miaosha.util.CookieUtil;
import com.miaosha.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/11/8.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping(value="/to_login",method= RequestMethod.GET)
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value="/do_login")
    @ResponseBody
    public ServerResponse login(@RequestParam(value="mobile") Long user, String password, HttpSession session, HttpServletResponse httpServletResponse){
        ServerResponse result=userService.login(user,password);
        if (!result.isSuccess()){
            //登录不成功，展示错误信息
        }

        CookieUtil.writeloginToken(httpServletResponse,session.getId());
        String key= MiaoshaUserKey.token.getPrefix()+session.getId();
        String userInfo= JsonUtil.obj2Str(result.getData());
        redisTemplate.opsForValue().set(String.format(key),userInfo,RedisConstant.EXPIRE,TimeUnit.SECONDS );
        return result;
    }
}
