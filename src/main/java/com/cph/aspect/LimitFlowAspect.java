package com.cph.aspect;

import com.cph.common.CommonResult;
import com.cph.utils.RedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;


@Aspect
@Component
public class LimitFlowAspect {

    @Pointcut("@annotation(com.cph.aspect.LimitFlow)")
    private void pointCutMethodController() {
    }

    /**
     * 环绕通知前后增强
     */
    @Around(value = "pointCutMethodController()")
    public Object doAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        LimitFlow limitFlow = method.getAnnotation(LimitFlow.class);
        if (limitFlow != null) {
            int interval = limitFlow.interval();
            int limit = limitFlow.limit();

            if(!RedisUtils.limitFlow("limit", limit, interval)){
                return new CommonResult(500, "操作频繁，请稍后再试", null, null);
            }
        }
        Object result = joinPoint.proceed();
        return result;
    }
}