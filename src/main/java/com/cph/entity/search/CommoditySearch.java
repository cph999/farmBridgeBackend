package com.cph.entity.search;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommoditySearch extends BaseSearch{
    private String search;
}
