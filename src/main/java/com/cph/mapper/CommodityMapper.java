package com.cph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cph.entity.Commodity;
import com.cph.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
}
