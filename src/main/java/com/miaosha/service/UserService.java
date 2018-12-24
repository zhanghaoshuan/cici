package com.miaosha.service;

import com.miaosha.common.ServerResponse;
import com.miaosha.pojo.MiaoshaUser;

/**
 * Created by Administrator on 2018/11/8.
 */
public interface UserService {
    public ServerResponse login(Long userId,String password);
    MiaoshaUser getByToken(String token);
}
