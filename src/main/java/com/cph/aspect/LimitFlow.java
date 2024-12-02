package com.cph.aspect;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE}) // 可以考虑应用到类上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitFlow {
    /**
     * 限流间隔
     * @return
     */
    int interval() default 60 * 1000;

    /**
     * 在interval内可以执行的次数
     * @return
     */
    int limit() default 10;
}
