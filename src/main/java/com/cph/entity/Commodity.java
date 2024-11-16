package com.cph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain =true)
@TableName("commodity")
public class Commodity {
    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;
    //商品名称
    private String productName;
    //商品描述
    private String description;
    //商品总数量
    private Integer amount;
    //商品单价
    private Double unitPrice;
    //商品库存
    private Integer stock;
    //卖家用户id
    private Integer userId;
    //发布时间
    private Date createdTime;

    private String images;
}
