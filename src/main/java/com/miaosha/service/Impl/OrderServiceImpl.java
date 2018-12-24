package com.miaosha.service.Impl;

import com.miaosha.dao.MiaoshaOrderMapper;
import com.miaosha.dao.OrderInfoMapper;
import com.miaosha.pojo.MiaoshaOrder;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.OrderInfo;
import com.miaosha.redis.OrderKey;
import com.miaosha.redis.RedisService;
import com.miaosha.service.OrderService;
import com.miaosha.util.JsonUtil;
import com.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018/11/14.
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaOrderMapper miaoshaOrderMapper;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId){
       // MiaoshaOrder result=orderInfoMapper.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        String key=OrderKey.getMiaoshaOrderByUidGid.getPrefix()+userId+"_"+goodsId;
        String result=redisService.get( key);
        MiaoshaOrder realResult=JsonUtil.str2Obj(result,MiaoshaOrder.class);
        return realResult;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderInfoMapper.selectByPrimaryKey(orderId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(BigDecimal.valueOf(goods.getMiaoshaPrice()));
        orderInfo.setOrderChannel((byte)1);
        orderInfo.setStatus((byte)1);
        orderInfo.setUserId(user.getId());
        orderInfoMapper.insertSelectiveReturnId(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrderMapper.insert(miaoshaOrder);
        String key=OrderKey.getMiaoshaOrderByUidGid.getPrefix()+user.getId()+"_"+goods.getId();
        String value= JsonUtil.obj2Str(miaoshaOrder);
        redisService.set(key,value);

        return orderInfo;

    }
}
