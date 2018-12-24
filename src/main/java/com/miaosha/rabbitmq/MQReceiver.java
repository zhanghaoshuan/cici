package com.miaosha.rabbitmq;

import com.miaosha.pojo.MiaoshaOrder;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.redis.RedisService;
import com.miaosha.service.GoodsService;
import com.miaosha.service.MiaoshaService;
import com.miaosha.service.OrderService;
import com.miaosha.util.JsonUtil;
import com.miaosha.vo.GoodsVo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/15.
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @RabbitListener(bindings=@QueueBinding(
            value=@Queue(value="order-queue",durable="true"),
            exchange = @Exchange(name="order-exchange",durable="true",type="topic"),
            key="order.#"
    ))
    @RabbitHandler
    public void receive(@Payload MiaoshaMessage msg, Channel channel, @Headers Map<String,Object> header){
//        log.info("receive message:"+msg);
//        MiaoshaMessage mm= JsonUtil.str2Obj(msg,MiaoshaMessage.class);
        MiaoshaUser user = msg.getUser();
        long goodsId = msg.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
        Long deliver=(Long)header.get(AmqpHeaders.DELIVERY_TAG);
        try {
            channel.basicAck(deliver,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
