package com.miaosha.service.Impl;

import com.miaosha.common.ServerResponse;
import com.miaosha.dao.MiaoshaUserMapper;
import com.miaosha.dao.UserMapper;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.User;
import com.miaosha.redis.MiaoshaUserKey;
import com.miaosha.redis.RedisService;
import com.miaosha.service.UserService;
import com.miaosha.util.CookieUtil;
import com.miaosha.util.JsonUtil;
import com.miaosha.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018/11/8.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserMapper miaoshaUserMapper;
    public ServerResponse login(Long userId,String password){
        if(userId==null||StringUtils.isEmpty(password)){
            return null;
        }
        MiaoshaUser miaoshaUser=miaoshaUserMapper.selectByPrimaryKey(userId);
        String MD5password= MD5Util.MD5EncodeUtf8(password+miaoshaUser.getSalt()).toLowerCase();
        if(miaoshaUser==null){
            //该id没有用户
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        if(!StringUtils.equals(MD5password,miaoshaUser.getPassword())){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        miaoshaUser.setPassword("");
        return ServerResponse.createBySuccess("登录成功",miaoshaUser);

    }
    public MiaoshaUser getByToken( String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        String key=MiaoshaUserKey.token.getPrefix()+token;
        String vaule=redisService.get(key);
        redisService.setEx(key,vaule,MiaoshaUserKey.token.expireSeconds());
        MiaoshaUser result=JsonUtil.str2Obj(vaule,MiaoshaUser.class);
        return result;
    }
}
