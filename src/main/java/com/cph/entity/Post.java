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

}   