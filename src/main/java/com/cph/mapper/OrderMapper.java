package com.cph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cph.entity.OrderMake;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderMake> {
}
