package com.cph.aspect;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cph.entity.User;
import com.cph.mapper.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
public class LoginAspect {

    @Autowired
    UserMapper userMapper;

    /**
     * 声明切面点拦截那些类
     */
    @Pointcut("@annotation(com.cph.aspect.RecognizeAddress)")
    private void pointCutMethodController() {
    }

    /**
     * 环绕通知前后增强
     */
    @Around(value = "pointCutMethodController()")
    public Object doAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();

            String token = request.getHeader("authorization");
            if (StringUtils.isBlank(token)) {
                throw new Exception("请先登录");
            }
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("token", token));

            if (user == null) throw new Exception("请先登录");
            user.setLastLoginTime(new Date());
            UserContext.setCurrentUser(user);
            userMapper.updateById(user);
        }
        // 执行方法
        Object result = joinPoint.proceed();
        return result;
    }


}
