package com.miaosha.dao;

import com.miaosha.pojo.MiaoshaOrder;
import com.miaosha.pojo.OrderInfo;

public interface OrderInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);

    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long UserId,Long Goodsid);

    int insertSelectiveReturnId(OrderInfo record);
}