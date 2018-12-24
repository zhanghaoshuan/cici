package com.miaosha.controller;

import com.miaosha.access.AccessLimit;
import com.miaosha.common.ServerResponse;
import com.miaosha.enums.CodeMsg;
import com.miaosha.rabbitmq.MQSender;
import com.miaosha.rabbitmq.MiaoshaMessage;
import com.miaosha.redis.GoodsKey;
import com.miaosha.redis.RedisService;
import com.miaosha.util.CookieUtil;
import com.miaosha.util.JsonUtil;
import com.miaosha.vo.GoodsVo;
import com.miaosha.pojo.MiaoshaGoods;
import com.miaosha.pojo.MiaoshaOrder;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.OrderInfo;
import com.miaosha.service.GoodsService;
import com.miaosha.service.MiaoshaService;
import com.miaosha.service.OrderService;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018/11/12.
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private RedisService redisService;

    @Autowired
    MQSender sender;

    private HashMap<Long,Boolean> localOverMap=new HashMap<>();

    @RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<OrderInfo> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId){
        model.addAttribute(user);
        GoodsVo goodsVo=goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount()<=0){
            return ServerResponse.createByErrorCodeMessage(CodeMsg.MIAO_SHA_OVER.getCode(),CodeMsg.MIAO_SHA_OVER.getMsg());
        }
        MiaoshaOrder miaoshaOrder=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder==null){
            return ServerResponse.createByErrorCodeMessage(CodeMsg.MIAOSHA_FAIL.getCode(),CodeMsg.MIAOSHA_FAIL.getMsg());
        }
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        return ServerResponse.createBySuccessData(orderInfo);

    }

    @RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse miaosha2(HttpServletRequest httpServletRequest,Model model, @RequestParam("goodsId")long goodsId,@PathVariable("path") String path,MiaoshaUser user){

//        String token= CookieUtil.getCookieToken(httpServletRequest);
//        String user1=redisService.get(token);
//        MiaoshaUser user= JsonUtil.str2Obj(user1,MiaoshaUser.class);

        model.addAttribute("user", user);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(CodeMsg.SESSION_ERROR);
        }

        boolean checkPathResult=miaoshaService.checkPath(user,goodsId,path);
        if(!checkPathResult){
            return ServerResponse.createByErrorCodeMsg(CodeMsg.REQUEST_ILLEGAL);
        }

        Boolean flag=localOverMap.get(goodsId);
        if(flag){
            return ServerResponse.createByErrorCodeMsg(CodeMsg.MIAO_SHA_OVER);
        }
        String key=GoodsKey.getMiaoshaGoodsStock.getPrefix()+goodsId;
        Long stock=redisService.decr(key);
        if(stock<0){
            localOverMap.put(goodsId, true);
            return ServerResponse.createByErrorCodeMsg(CodeMsg.MIAO_SHA_OVER);
        }
        MiaoshaMessage message=new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setUser(user);
        message.setMessgeId(System.currentTimeMillis()+"$"+ UUID.randomUUID().toString());
        sender.sendMiaoshaMessage(message);
        return ServerResponse.createBySuccessMessage("");
    }
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    ){
        if (user==null){
            return ServerResponse.createByErrorCodeMsg(CodeMsg.SESSION_ERROR);
        }
        boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return ServerResponse.createByErrorCodeMsg(CodeMsg.REQUEST_ILLEGAL);
        }
        String path  =miaoshaService.createMiaoshaPath(user, goodsId);
        return ServerResponse.createBySuccessData(path);
    }

    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId")long goodsId){

        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image=miaoshaService.createVerifyCode(user,goodsId);
        try {
            OutputStream outputStream=response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMsg(CodeMsg.MIAOSHA_FAIL);
        }
    }




    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList=goodsService.listGoodsVo();
        if(goodsVoList==null){
            return;
        }
        for(GoodsVo g:goodsVoList){
            String key= GoodsKey.getMiaoshaGoodsStock.getPrefix()+g.getId();
            redisService.set(key,g.getStockCount().toString());
            localOverMap.put(g.getId(),false);
        }
    }
}
