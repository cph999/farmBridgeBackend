package com.cph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cph.aspect.RecognizeAddress;
import com.cph.common.CommonResult;
import com.cph.entity.Commodity;
import com.cph.entity.Post;
import com.cph.entity.search.CommoditySearch;
import com.cph.mapper.CommodityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommodityController {
    //分页查询
    @Autowired
    CommodityMapper commodityMapper;

    @RequestMapping("/getCommodityList")
    @RecognizeAddress
    public CommonResult getCommodities(@RequestBody CommoditySearch commoditySearch){
        LambdaQueryWrapper<Commodity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(commoditySearch.getSearch()),Commodity::getProductName,commoditySearch.getSearch());
        wrapper.orderByDesc(Commodity::getCreatedTime);
        Page<Commodity> postPage = new Page<>(commoditySearch.getPageNum(), commoditySearch.getPageSize());
        Page<Commodity> resultPage = commodityMapper.selectPage(postPage, wrapper);
        return new CommonResult(200, "查询成功", null, resultPage.getRecords(), resultPage.getTotal());
    }
}
