package com.cph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("post_bid")
public class PostBid {
    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;

    private Integer fromId;
    private Integer toId;
    private Integer orderId;

    private String username;
    private String password;
    private String email;
    private String nickname;
    private String cover;
    private String token;
    private String phone;
    private String address;
    private int userId;
    private String userCover;
    private Date bidTime;
    private String description;
    private String images;
    private String locationCoordinate;
    private String locationInfo;
    private String categoryCode;
    private String categoryName;
    private String bidPrice;

    /**
     * 1: 正在报价
     * 2：报价结束 等待对方回应
     * 3：接受报价
     * 4：拒绝报价
     * 5: 订单完成
     */
    @TableField(value = "chat_restrict_state")
    private Integer chatRestrictState;
}