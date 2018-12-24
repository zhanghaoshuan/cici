package com.miaosha.access;

import com.miaosha.common.ServerResponse;
import com.miaosha.enums.CodeMsg;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.redis.AccessKey;
import com.miaosha.redis.RedisService;
import com.miaosha.service.UserService;
import com.miaosha.util.CookieUtil;
import com.miaosha.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by Administrator on 2018/11/17.
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            String token = CookieUtil.getCookieToken(request);
            MiaoshaUser user = userService.getByToken(token);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin == true) {
                if (user == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key = key + "_" + user.getId();
            } else {

            }
            AccessKey ak = AccessKey.withExpire(seconds);
            String key2 = ak.getPrefix() + key;
            String value2 = redisService.get(key2);
            if (value2 == null) {
                redisService.set(key2, String.valueOf(1));
                return true;
            }
            Integer value=Integer.valueOf(value2);
            if (value < maxCount) {
                redisService.incr(key2);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }
    private void render(HttpServletResponse response, CodeMsg codeMsg)throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JsonUtil.obj2Str(ServerResponse.createByErrorCodeMsg(codeMsg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
