package com.miaosha.service;

import com.miaosha.pojo.MiaoshaOrder;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.OrderInfo;
import com.miaosha.vo.GoodsVo;

/**
 * Created by Administrator on 2018/11/14.
 */

public interface OrderService {
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId);
    OrderInfo getOrderById(long orderId);
    OrderInfo createOrder(MiaoshaUser user, GoodsVo goods);
}
