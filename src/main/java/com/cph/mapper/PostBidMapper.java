package com.cph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cph.entity.PostBid;
import com.cph.entity.vo.PostBidVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mapper
public interface PostBidMapper extends BaseMapper<PostBid> {
    //查询近30天交易数据平均价格
    public List<PostBidVo> statisticData(@Param("qDate") Date qDate);
}
