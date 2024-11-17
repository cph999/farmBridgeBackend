package com.cph.entity.search;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderSearch extends BaseSearch{
    private Integer state;
}