package com.cph.config;

import com.cph.entity.Order;

/**
 * 全局配置
 */
public class GlobalConfig {

    public static final String VERIFICATION_CODE_DAY_PREFIX = "VERIFICATION_CODE_DAY_PREFIX";
    public static final String VERIFICATION_CODE_HOUR_PREFIX = "VERIFICATION_CODE_HOUR_PREFIX";
    public static final Integer VERIFICATION_CODE_VALID = 60;

    public static final String redisPrefix = "farmerBridge_";

    public static final String DEV = "dev";
    public static final String PROD = "prod";


    public static final String REDIS_LOCK_COMMODITY_ORDER = "REDIS_LOCK_COMMODITY_ORDER";

}
