package com.cph.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cph.aspect.RecognizeAddress;
import com.cph.aspect.UserContext;
import com.cph.common.CommonResult;
import com.cph.entity.Message;
import com.cph.entity.Post;
import com.cph.entity.PostBid;
import com.cph.entity.User;
import com.cph.entity.search.PostSearch;
import com.cph.mapper.PostBidMapper;
import com.cph.mapper.PostMapper;
import com.google.gson.Gson;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    PostMapper postMapper;

    @Autowired
    PostBidMapper postBidMapper;

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
    public CommonResult getPopularData() {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.last("limit 5").orderByDesc(Post::getCreatedTime);
        List<Post> posts = postMapper.selectList(wrapper);
        return new CommonResult(200, "查询成功", posts);
    }

    /**
     * 查询某个人是否对某个post报过价
     */
    @RequestMapping("/isBid")
    @RecognizeAddress
    public CommonResult queryIsBid(@RequestBody Message message) {
        if (message == null) return new CommonResult(500, "参数错误", null);
        if (!"bid".equals(message.getType())) return new CommonResult(500, "参数错误", null);
        Gson gson = new Gson();
        PostBid postBid = gson.fromJson(message.getMessage(), PostBid.class);
        postBid.setFromId(message.getFromId()).setToId(message.getToId());
        User currentUser = UserContext.getCurrentUser();
        LambdaQueryWrapper<PostBid> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostBid::getOrderId, postBid.getOrderId()).eq(PostBid::getFromId, currentUser.getId()).isNotNull(PostBid::getBidPrice);
        PostBid postBid1 = postBidMapper.selectOne(wrapper);
        return new CommonResult(200, "报价状态查询成功", postBid1);
    }


        /**
     * 查询某个人是否对某个post报过价
     */
    @RequestMapping("/addPost")
    @RecognizeAddress
    public CommonResult addPost(@RequestBody Post post) {
        User currentUser = UserContext.getCurrentUser();
        post.setUserId(currentUser.getId()).setUserCover(currentUser.getCover()).setCreatedTime(new Date());
        postMapper.insert(post);
        return new CommonResult(200, "报价状态查询成功", post);
    }
}