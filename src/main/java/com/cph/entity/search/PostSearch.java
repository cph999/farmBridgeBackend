package com.cph.entity.search;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostSearch {
    private String search;
    private String categoryCode;

    private Integer pageNum;
    private Integer pageSize;
}