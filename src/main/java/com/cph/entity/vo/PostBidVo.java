package com.cph.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostBidVo {
    private String updateTime;
    private Double bidPrice;
}