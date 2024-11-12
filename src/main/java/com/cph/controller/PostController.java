package com.cph.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cph.aspect.RecognizeAddress;
import com.cph.common.CommonResult;
import com.cph.entity.Post;
import com.cph.entity.search.PostSearch;
import com.cph.mapper.PostMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    PostMapper postMapper;

    private List<String> categories = Arrays.asList("all", "niu", "yang", "zhu", "ya");

    @RequestMapping("/getPostList")
    @RecognizeAddress
    public CommonResult getPostList(@RequestBody PostSearch postSearch) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isBlank(postSearch.getSearch()), Post::getDescription, postSearch.getSearch());
        if (!StringUtils.isBlank(postSearch.getCategoryCode())) {
            //不是全部
            if (!"0".equals(postSearch.getCategoryCode())) {
                wrapper.eq(Post::getCategoryCode, categories.get(Integer.parseInt(postSearch.getCategoryCode())));
            }
        }
        wrapper.orderByDesc(Post::getCreatedTime);
        Page<Post> postPage = new Page<>(postSearch.getPageNum(), postSearch.getPageSize());
        Page<Post> resultPage = postMapper.selectPage(postPage, wrapper);
        return new CommonResult(200, "查询成功", null, resultPage.getRecords(), resultPage.getTotal());
    }

    @RequestMapping("/popularData")
    public CommonResult getPopularData(){
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.last("limit 5").orderByDesc(Post::getCreatedTime);
        List<Post> posts = postMapper.selectList(wrapper);
        return new CommonResult(200,"查询成功",posts);
    }

    @RequestMapping("/getCategory")
    public CommonResult getCategory() {

        return new CommonResult(200, "success", postMapper.selectList(null));
    }
}