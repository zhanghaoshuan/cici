package com.miaosha.rabbitmq;

import com.miaosha.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/11/15.
 */
@Slf4j
@Service
public class MQSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage msg){
        String msg2string = JsonUtil.obj2Str(msg);
        log.error("秒杀的message已经发送如队列");
        CorrelationData correlationData=new CorrelationData();
        correlationData.setId(msg.getMessgeId());
        rabbitTemplate.convertAndSend("order-exchange","order.abcd",msg,correlationData);
//        rabbitTemplate.correlationConvertAndSend();
    }
}
