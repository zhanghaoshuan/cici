package com.miaosha.service.Impl;

import com.miaosha.dao.MiaoshaGoodsMapper;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.pojo.OrderInfo;
import com.miaosha.redis.MiaoshaKey;
import com.miaosha.redis.RedisService;
import com.miaosha.service.GoodsService;
import com.miaosha.service.MiaoshaService;
import com.miaosha.service.OrderService;
import com.miaosha.util.MD5Util;
import com.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by Administrator on 2018/11/12.
 */
@Service
public class MiaoshaServiceImpl implements MiaoshaService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    @Override
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods){
        goodsService.reduceStock(goods);
        return orderService.createOrder(user,goods);
    }

    public boolean checkVerifyCode(MiaoshaUser miaoshaUser,long goodsId,int verifyCode){
        if(miaoshaUser == null || goodsId <=0) {
            return false;
        }
        String key= MiaoshaKey.getMiaoshaVerifyCode.getPrefix()+ miaoshaUser.getId()+","+goodsId;
        Integer value=Integer.valueOf(redisService.get(key));
        if(value== null || value - verifyCode != 0 ) {
            return false;
        }
        redisService.del(key);
        return true;
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        String key=MiaoshaKey.getMiaoshaVerifyCode.getPrefix()+user.getId()+","+goodsId;
        redisService.set(key,String.valueOf(rnd));
        //输出图片
        return image;
    }

    private int calc(String verifyCode){
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String key=MiaoshaKey.getMiaoshaPath.getPrefix()+user.getId()+goodsId;
        String str = MD5Util.MD5EncodeUtf8(key);
        redisService.set(key, str);
        return str;
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String key=MiaoshaKey.getMiaoshaPath.getPrefix()+user.getId()+goodsId;
        String pathOld = redisService.get(key);
        return path.equals(pathOld);
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
