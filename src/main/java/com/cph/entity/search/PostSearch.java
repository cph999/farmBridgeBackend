package com.cph.entity.search;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostSearch extends BaseSearch{
    private String search;
    private String categoryCode;
}