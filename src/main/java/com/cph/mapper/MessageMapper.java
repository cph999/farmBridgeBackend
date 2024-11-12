package com.cph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cph.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
