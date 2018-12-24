package com.miaosha.access;

import com.miaosha.pojo.MiaoshaUser;

/**
 * Created by Administrator on 2018/11/17.
 */
public class UserContext {
    private static ThreadLocal<MiaoshaUser> threadLocal=new ThreadLocal<MiaoshaUser>();

    public static MiaoshaUser getUser() {
        return threadLocal.get();
    }

    public static void setUser(MiaoshaUser miaoshaUser) {
        threadLocal.set(miaoshaUser);
        return;
    }
}
