package com.cph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
//初步 一个订单只有一个sku
public class Order {
    //订单id
    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;
    //商品id
    private Integer cid;
    //购买人id
    private Integer uid;
    private Date createdTime;
    //备注
    private String description;
    //收货人地址信息
    private String locationInfo;
    //经纬度坐标
    private String locationCoordinate;
    private Integer oAmount;
    private Double totalPrice;

    //订单状态 0:已创建 1:已支付 2:交易失败 3：交易完成
    private Integer state;


}
