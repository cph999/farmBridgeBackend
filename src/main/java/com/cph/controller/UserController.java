package com.cph.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cph.common.CommonResult;
import com.cph.config.GlobalConfig;
import com.cph.entity.User;
import com.cph.entity.pojo.LoginUser;
import com.cph.mapper.UserMapper;
import com.cph.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Value("${env.name}")
    private String env;

    @PostMapping("/login")

    public CommonResult login(@RequestBody LoginUser loginUser) {
        if(GlobalConfig.DEV.equals(env)){
            //登录或注册
            User currentUser = userMapper.selectOne(new QueryWrapper<User>().eq("phone", loginUser.getPhone()));
            if(currentUser != null){
                currentUser.setToken(UUID.randomUUID().toString()).setLastLoginTime(new Date());
                userMapper.updateById(currentUser);
                return new CommonResult(200,"登陆成功",currentUser);
            }else{
                //注册
                User user = new User();
                user.setToken(UUID.randomUUID().toString());
                user.setNickname("小牧").setCover("https://app102.acapp.acwing.com.cn/media/1729307339239.png").setPhone(loginUser.getPhone())
                        .setLastLoginTime(new Date()).setUsername("xiaomu_"+  (Math.random() * 10001));
                userMapper.insert(user);
                return new CommonResult(200,"登陆成功",user);
            }
        }
        String key = GlobalConfig.VERIFICATION_CODE_HOUR_PREFIX + loginUser.getPhone();
        String code = (String) RedisUtils.get(key);
        if(StringUtils.isBlank(code)) return new CommonResult<>(500, "验证码已过期，请重新获取", null);
        if(loginUser.getVerificationCode().equals(code)){
            //登录或注册
            User currentUser = userMapper.selectOne(new QueryWrapper<User>().eq("phone", loginUser.getPhone()));
            if(currentUser != null){
                currentUser.setToken(UUID.randomUUID().toString()).setLastLoginTime(new Date());
                userMapper.updateById(currentUser);
                return new CommonResult(200,"登陆成功",currentUser);
            }else{
                //注册
                User user = new User();
                user.setToken(UUID.randomUUID().toString());
                user.setNickname("小牧").setCover("https://app102.acapp.acwing.com.cn/media/1729307339239.png").setPhone(loginUser.getPhone())
                        .setLastLoginTime(new Date()).setUsername("xiaomu_"+  (Math.random() * 10001));
                userMapper.insert(user);
                return new CommonResult(200,"登陆成功",user);
            }
        }
        return new CommonResult(200,"登陆成功",null);
    }


    @GetMapping("/generateVC/{phone}")
    public CommonResult<String> generateVC(@PathVariable("phone") String phone) {
        int randomNumber = (int) (Math.random() * 10001);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String timeValue = simpleDateFormat.format(date);

        String s = (String) RedisUtils.get(GlobalConfig.VERIFICATION_CODE_HOUR_PREFIX + phone);
        if (!StringUtils.isBlank(s)) {
            return new CommonResult<>(500, "请勿重复获取验证码", null);
        }

        Long ds = RedisUtils.incr(GlobalConfig.VERIFICATION_CODE_DAY_PREFIX + timeValue + "_" + phone, 1);
        if (ds > 10) {
            return new CommonResult<>(500, "今日验证码已超过10次，明日再试", null);
        }

        String verificationCodeKey = GlobalConfig.VERIFICATION_CODE_HOUR_PREFIX + phone;
        RedisUtils.set(verificationCodeKey, String.valueOf(randomNumber), GlobalConfig.VERIFICATION_CODE_VALID);

        String dailyRequestKey = GlobalConfig.VERIFICATION_CODE_DAY_PREFIX + timeValue + "_" + phone;
        RedisUtils.expire(dailyRequestKey, 86400L);  // 设置有效期为24小时

        return new CommonResult<>(200,"验证码发送成功",phone);
    }

}