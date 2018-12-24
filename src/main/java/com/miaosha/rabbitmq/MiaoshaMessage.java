package com.miaosha.rabbitmq;

import com.miaosha.pojo.MiaoshaUser;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/11/15.
 */
@Data
public class MiaoshaMessage implements Serializable{
    private MiaoshaUser user;
    private long goodsId;
    private String messgeId;
}
