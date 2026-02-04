package com.atguigu.examsystem.util;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private static RedisTemplate<String, Object> redisTemplate;

    /* ============================= String类型 start ============================= */
    /**
     * 设置key-value
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        // opsForValue是对字符串类型操作
        redisTemplate.opsForValue().set(key,value);
    }

    /**
     * 设置k-v 过期时间second秒
     * @param key
     * @param value
     * @param second
     */
    public static void set(String key, Object value, long second) {
        redisTemplate.opsForValue().set(key, value, second, TimeUnit.SECONDS);
    }

    /**
     * 获取key对应的value值
     * @param key
     * @return
     */
    public static Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除key缓存
     * @param key
     */
    public static void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除keyList缓存
     * @param keyList
     */
    public static void deleteBatch(Collection<String> keyList) {
        redisTemplate.delete(keyList);
    }

    /**
     * 设置key的过期时间
     * @param key
     * @param second
     */
    public static void expire(String key, long second) {
        redisTemplate.expire(key, second, TimeUnit.SECONDS);
    }

    /**
     * 是否存在key
     * @param key
     * @return
     */
    public static Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 根据pattern获取所有匹配的key
     * @param pattern
     * @return
     */
    public static Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 根据pattern批量删除匹配到的key
     * @param pattern
     */
    public static void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (!CollUtil.isEmpty(keys)) {
                deleteBatch(keys);
        }
    }

    /* ============================= String类型 end ============================= */

    /* ============================= Hash类型 start ============================= */

    /**
     * 设置hash缓存
     * @param key
     * @param hashKey
     * @param value
     */
    public static void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取hash值
     * @param key
     * @param hashKey
     * @return
     */
    public static Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除hash值
     * @param key
     * @param hashKey
     * @return
     */
    public static Long hDelete(String key, Object... hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 设置hash值
     * @param key
     * @param map
     */
    public static void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 根据key获取到<field,value>集合
     * @param key
     * @return
     */
    public static Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 判断是否存在key中是否存在hashKey字段
     * @param key
     * @param hashKey
     * @return
     */
    public static Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /* ============================= Hash类型 end ============================= */

}
