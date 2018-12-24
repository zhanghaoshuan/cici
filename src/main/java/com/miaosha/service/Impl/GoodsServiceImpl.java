package com.miaosha.service.Impl;

import com.miaosha.dao.GoodsMapper;
import com.miaosha.dao.MiaoshaGoodsMapper;
import com.miaosha.pojo.MiaoshaGoods;
import com.miaosha.vo.GoodsVo;
import com.miaosha.pojo.Goods;
import com.miaosha.service.GoodsService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/11/8.
 */
@Service
public class GoodsServiceImpl implements GoodsService{
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private MiaoshaGoodsMapper miaoshaGoodsMapper;

    public List<GoodsVo> listGoodsVo(){
        return goodsMapper.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public MiaoshaGoods getMiaoshaGoodsByGoodsId(@Param("goodsId") long goodsId) {
        return miaoshaGoodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goods){
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        int ret = miaoshaGoodsMapper.reduceStock(g);
        return ret > 0;
    }
}
