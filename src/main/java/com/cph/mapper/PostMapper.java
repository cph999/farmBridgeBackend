package com.cph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cph.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}