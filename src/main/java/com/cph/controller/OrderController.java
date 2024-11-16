package com.cph.controller;

import com.cph.aspect.RecognizeAddress;
import com.cph.aspect.UserContext;
import com.cph.common.CommonResult;
import com.cph.config.GlobalConfig;
import com.cph.entity.Commodity;
import com.cph.entity.User;
import com.cph.mapper.CommodityMapper;
import com.cph.mapper.OrderMapper;
import com.cph.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    CommodityMapper commodityMapper;

    @Autowired
    OrderMapper orderMapper;

    @PostMapping("/createOrder")
    @RecognizeAddress
    public CommonResult createOrder(@RequestBody Commodity commodity){
        User currentUser = UserContext.getCurrentUser();
        try{
            boolean lock = RedisUtils.lock(GlobalConfig.REDIS_LOCK_COMMODITY_ORDER + commodity.getId(), currentUser.getId());
            if(lock){

            }else{
                return new CommonResult(501,"系统繁忙,请稍后再试",null);
            }
        }catch (Exception e){
            return new CommonResult(502,"系统出错，请联系运维人员",null);
        }finally {
            RedisUtils.unlock(GlobalConfig.REDIS_LOCK_COMMODITY_ORDER + commodity.getId());
        }
        return new CommonResult();
    }
}
