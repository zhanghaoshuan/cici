package com.miaosha.controller;

import com.miaosha.common.ServerResponse;
import com.miaosha.vo.GoodsVo;
import com.miaosha.pojo.MiaoshaUser;
import com.miaosha.redis.GoodsKey;
import com.miaosha.redis.RedisService;
import com.miaosha.service.GoodsService;
import com.miaosha.util.CookieUtil;
import com.miaosha.util.JsonUtil;
import com.miaosha.vo.GoodsDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/8.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

//    @Autowired
//    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;
    @RequestMapping(value="to_list")
    @ResponseBody
    public  String list(HttpServletRequest httpServletRequest,HttpServletResponse response,Model model){
        String key=GoodsKey.getGoodsList.getPrefix();
        String value=redisService.get(key);
        if(!StringUtils.isEmpty(value)){
            return value;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);

        String token=CookieUtil.getCookieToken(httpServletRequest);
        String user1=redisService.get(token);
        MiaoshaUser user=JsonUtil.str2Obj(user1,MiaoshaUser.class);
        model.addAttribute("user",user);
        WebContext springWebContext = new WebContext(httpServletRequest,response,httpServletRequest.getServletContext(),httpServletRequest.getLocale(),model.asMap());
        String html=thymeleafViewResolver.getTemplateEngine().process("goods_list",springWebContext);
        if (!StringUtils.isEmpty(html)){
            redisService.set(key,html);
        }
        return html;


//        String token=CookieUtil.getCookieToken(httpServletRequest);
//        String user1=redisTemplate.opsForValue().get(token);
//        MiaoshaUser user=JsonUtil.str2Obj(user1,MiaoshaUser.class);
//        map.put("goodsList", goodsList);
//        map.put("user",user);
//        return new ModelAndView("goods_list",map);
    }


    @RequestMapping(value="/to_detail/{goodsId}",produces="text/html")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                  @PathVariable("goodsId")long goodsId){

        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus,remainSeconds;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        ServerResponse.createBySuccessData(vo);
        return ServerResponse.createBySuccessData(vo);
    }

    @RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
                          @PathVariable("goodsId")long goodsId){
        model.addAttribute("user", user);
        String key= GoodsKey.getGoodsDetail.getPrefix()+goodsId;
        String value=redisService.get(key);
        if (!StringUtils.isEmpty(value)){
            return value;
        }
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);


        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        WebContext springWebContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        String html=thymeleafViewResolver.getTemplateEngine().process("goods_detail",springWebContext);
        if(!StringUtils.isEmpty(html)) {
            redisService.set( key , html);
        }
        return html;
    }

    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public ServerResponse<GoodsDetailVo> detail3(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,@PathVariable(value="goodsId")long goodsId) {
        //long goodsId=1;
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return ServerResponse.createBySuccessData(vo);
    }
}
