package com.cph.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cph.aspect.RecognizeAddress;
import com.cph.aspect.UserContext;
import com.cph.common.CommonResult;
import com.cph.config.GlobalConfig;
import com.cph.entity.Commodity;
import com.cph.entity.OrderMake;
import com.cph.entity.PostBid;
import com.cph.entity.User;
import com.cph.entity.search.OrderSearch;
import com.cph.mapper.CommodityMapper;
import com.cph.mapper.OrderMapper;
import com.cph.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    CommodityMapper commodityMapper;

    @Autowired
    OrderMapper orderMapper;

    @PostMapping("/createOrder")
    @RecognizeAddress
    @Transactional
    public CommonResult createOrder(@RequestBody Commodity commodity) {
        User currentUser = UserContext.getCurrentUser();
        Integer stock = commodityMapper.selectById(commodity.getId()).getStock();
        try {
            boolean lock = RedisUtils.lock(GlobalConfig.REDIS_LOCK_COMMODITY_ORDER + commodity.getId(), currentUser.getId());
            if (lock) {
                commodity.setStock(stock - commodity.getCurrentAmount());
                commodityMapper.updateById(commodity);
                OrderMake orderMake = new OrderMake();
                orderMake.setCid(commodity.getId()).setUid(currentUser.getId()).setState(1).setOAmount(commodity.getCurrentAmount()).setLocationInfo("测试地址")
                        .setCreatedTime(new Date()).setCommodityName(commodity.getProductName()).setCommodityImages(commodity.getImages())
                        .setCommodityDescription(commodity.getDescription()).setTotalPrice(commodity.getUnitPrice() * commodity.getCurrentAmount())
                        .setUnitPrice(commodity.getUnitPrice()).setLocationInfo(currentUser.getAddress());
                orderMapper.insert(orderMake);
                return new CommonResult(200, "下单成功", orderMake);
            } else {
                return new CommonResult(501, "系统繁忙,请稍后再试", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult(502, "系统出错，请联系运维人员", null);
        } finally {
            RedisUtils.unlock(GlobalConfig.REDIS_LOCK_COMMODITY_ORDER + commodity.getId());
        }
    }
    /**
     * 模拟支付成功
     * @param order
     * @return
     */
    @PostMapping("/payState")
    @RecognizeAddress
    public CommonResult payState(@RequestBody OrderMake order) {
        if (order.getState() == 1) {
            order.setState(2);
            orderMapper.updateById(order);
            return new CommonResult(200, "支付成功", order);
        } else {
            return new CommonResult(501, "订单已支付", null);
        }
    }

    /**
     * 查询订单
     * @return
     */
    @PostMapping("/getOrders")
    @RecognizeAddress
    public CommonResult getOrders(@RequestBody OrderSearch orderSearch) {
        User currentUser = UserContext.getCurrentUser();
        QueryWrapper<OrderMake> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",currentUser.getId()).eq(0 != orderSearch.getState(),"state",orderSearch.getState());
        Page<OrderMake> orderPages = new Page<>(orderSearch.getPageNum(), orderSearch.getPageSize());
        Page<OrderMake> orderMakePage = orderMapper.selectPage(orderPages, wrapper);
        return new CommonResult(200, "查询成功", null, orderMakePage.getRecords(), orderMakePage.getTotal());
    }
}
