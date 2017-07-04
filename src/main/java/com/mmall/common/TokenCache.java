package com.mmall.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by ztian on 2017/6/3.
 * 本地token缓存类
 */
public class TokenCache {
    private static Logger logger= LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX="token_";
    //生成LoadingCache 初始容量1000 最大容量10000 元素有效时间2小时 找不到默认元素时返回"null"
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().
            initialCapacity(1000).maximumSize(10000).expireAfterAccess(2, TimeUnit.HOURS).
            build(
                    //在localCache中根据key找不到value值时返回什么值
                    new CacheLoader<String, String>(){
                @Override
                public String load(String s) throws Exception {
                    return null;
                }
            });
    public static void  setKey(String key,String value){
        localCache.put(key,value);
    }
    public static String getKey(String key){
        String value=null;
        try {
            value=localCache.get(key);
            return value;
        }catch (Exception e)
        {
            logger.error("localcache error",e);
        }
        return null;
    }
}
