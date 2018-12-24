package com.miaosha.controller;

import com.miaosha.common.ServerResponse;
import com.miaosha.enums.CodeMsg;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.OrderInfo;
import com.miaosha.service.GoodsService;
import com.miaosha.service.OrderService;
import com.miaosha.vo.GoodsVo;
import com.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2018/11/14.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("detail")
    @ResponseBody
    public ServerResponse<OrderDetailVo> info(Model model, MiaoshaUser miaoshaUser, @RequestParam("orderId")long orderId){
        if(miaoshaUser==null){
            return ServerResponse.createByErrorCodeMessage(CodeMsg.SESSION_ERROR.getCode(),CodeMsg.SESSION_ERROR.getMsg());
        }
        OrderInfo order=orderService.getOrderById(orderId);
        if(order==null){
            return ServerResponse.createByErrorCodeMessage(CodeMsg.ORDER_NOT_EXIST.getCode(),CodeMsg.ORDER_NOT_EXIST.getMsg());
        }
        long goodsId=order.getGoodsId();
        GoodsVo goodsVo=goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo result=new OrderDetailVo();
        result.setGoods(goodsVo);
        result.setOrder(order);
        return ServerResponse.createBySuccessData(result);
    }
}
