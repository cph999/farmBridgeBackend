package com.cph.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain =true)
@TableName("commodity")
public class Commodity {

    private Integer id;

    private String productName;
    private String description;
    private Integer amount;
    private Double unitPrice;
    private Integer stock;

    private Integer userId;
    private Date createdTime;
}
