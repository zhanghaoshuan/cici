package com.miaosha.service;

import com.miaosha.dao.MiaoshaGoodsMapper;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.OrderInfo;
import com.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;

/**
 * Created by Administrator on 2018/11/12.
 */
public interface MiaoshaService {
    OrderInfo miaosha(MiaoshaUser user, GoodsVo goods);
    boolean checkVerifyCode(MiaoshaUser miaoshaUser,long goodsId,int verifyCode);
    BufferedImage createVerifyCode(MiaoshaUser user, long goodsId);
    boolean checkPath(MiaoshaUser user, long goodsId, String path);
    String createMiaoshaPath(MiaoshaUser user, long goodsId);
}
