package com.miaosha.service;

import com.miaosha.pojo.MiaoshaGoods;
import com.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/11/12.
 */
public interface GoodsService {
    List<GoodsVo> listGoodsVo();
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);
    MiaoshaGoods getMiaoshaGoodsByGoodsId(@Param("goodsId") long goodsId);
    boolean reduceStock(GoodsVo goods);
}
