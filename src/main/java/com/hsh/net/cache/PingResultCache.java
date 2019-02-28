package com.hsh.net.cache;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.hsh.net.bean.PingResultBean;

/**
 * TODO
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/27 15:00
 */
public final class PingResultCache {
    
    private final static Cache<String, PingResultBean> CACHE = CacheUtil.newFIFOCache(1024);
    
    private PingResultCache() {
    }
    
    /**
     * 获取ping结果缓存
     *
     * @param key key
     * @return ping结果缓存
     */
    public static PingResultBean get(String key) {
        return CACHE.get(key);
    }
    
    /**
     * 放置ping结果缓存
     *
     * @param key key
     * @param pingResultBean ping结果
     */
    public static void put(String key, PingResultBean pingResultBean) {
        CACHE.put(key, pingResultBean);
    }
    
}
