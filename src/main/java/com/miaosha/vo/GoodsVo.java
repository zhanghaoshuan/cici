package com.miaosha.vo;

import com.miaosha.pojo.Goods;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/8.
 */
@Data
public class GoodsVo extends Goods{
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
