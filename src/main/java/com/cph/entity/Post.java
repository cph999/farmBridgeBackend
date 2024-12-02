package com.cph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Post {

    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;

    private Integer userId;

    private String userCover;

    private Date createdTime;
    private String description;
    private String images;

    private String locationCoordinate;
    private String locationInfo;

    private String categoryCode;
    private String categoryName;

    private String cowBreed;  // 牛源品种
    private String cowSex;    // 牛源性别
    private Integer cowAge;   // 牛源月龄
    private String purchaseLocation; // 采购位置
    private Integer purchaseQuantity; // 采购数量
    private Double purchaseUnitPrice; // 采购单价
    private Double averageWeight;  // 牛只均重
    private Date deliveryDate;    // 交货期
    private Integer purchaseCycle; // 采购周期
    private String contactPhone;  // 联系电话
}
