package com.atguigu.examsystem.util;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
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
     *
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        // opsForValue是对字符串类型操作
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置k-v 过期时间second秒
     *
     * @param key
     * @param value
     * @param second
     */
    public static void set(String key, Object value, long second) {
        redisTemplate.opsForValue().set(key, value, second, TimeUnit.SECONDS);
    }

    /**
     * 获取key对应的value值
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除key缓存
     *
     * @param key
     */
    public static void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除keyList缓存
     *
     * @param keyList
     */
    public static void deleteBatch(Collection<String> keyList) {
        redisTemplate.delete(keyList);
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param second
     */
    public static void expire(String key, long second) {
        redisTemplate.expire(key, second, TimeUnit.SECONDS);
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    public static Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 根据pattern获取所有匹配的key
     *
     * @param pattern
     * @return
     */
    public static Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 根据pattern批量删除匹配到的key
     *
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
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public static void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取hash值
     *
     * @param key
     * @param hashKey
     * @return
     */
    public static Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除hash值
     *
     * @param key
     * @param hashKey
     * @return
     */
    public static Long hDelete(String key, Object... hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 设置hash值
     *
     * @param key
     * @param map
     */
    public static void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 根据key获取到<field,value>集合
     *
     * @param key
     * @return
     */
    public static Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 判断是否存在key中是否存在hashKey字段
     *
     * @param key
     * @param hashKey
     * @return
     */
    public static Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /* ============================= Hash类型 end ============================= */


    /* ============================= List类型 start ============================ */

    /**
     * 将value值放入key键中
     *
     * @param key
     * @param value
     * @return
     */
    public static long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将value值放入缓存，超过timeout秒则执行超时
     *
     * @param key
     * @param value
     * @param timeout 超时时间
     * @return
     */
    public static long lPush(String key, Object value, long timeout) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        expire(key, timeout);
        return count;
    }

    /**
     * 将values放入缓存
     *
     * @param key
     * @param values
     * @return
     */
    public static long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 将values放入缓存，超过timeout秒执行超时
     *
     * @param key
     * @param timeout 超时时间
     * @param values
     * @return
     */
    public static long lPushAll(String key, long timeout, Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, timeout);
        return count;
    }

    /**
     * 查出key索引从start到end的值，包含start和end
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取key的值的长度
     *
     * @param key
     * @return
     */
    public static long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取指定index位置的key的值
     *
     * @param key
     * @param index
     * @return
     */
    public static Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 移除指定数量的key值为value的值
     *
     * @param key
     * @param count
     * @param value
     * @return 返回真正移除value值的数量
     */
    public static Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /* ============================= List类型 end ============================ */

    /* ============================= Zset类型 start ============================ */

    /**
     * 向有序集合中添加元素，如果元素已存在，则更新分数
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 增添key键值为value的score分数，增加delta，delta值可以是负数
     *
     * @param key
     * @param value
     * @param delta
     * @return 修改后的score数据
     */
    public Double zAdd(String key, Object value, long delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回key的值为value的分数
     *
     * @param key
     * @param value
     * @return score分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 返回key中value值的数量
     *
     * @param key
     * @return
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 根据score最小值和最大值，获取key的值的集合
     *
     * @param key
     * @param min
     * @param max
     * @return key的值的集合
     */
    public Set<Object> zRangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 返回value值的索引位置
     *
     * @param key
     * @param value
     * @return
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }


    /**
     * 获取key的值的集合，按score从低到高排序（默认排序按score值从低到高）
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }


    /**
     * 获取key的值和score分数的集合，按score从低到高排序（默认排序按score值从低到高）
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 获取key的值的集合，按score从高到低排序（倒序排序）
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }


    /**
     * 获取key的值的集合，按score从高到低排序（倒序排序）
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 删除key的value集合值，返回删除数量
     * @param key
     * @param values
     * @return
     */
    public long zRemove(String key, Object ...values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /* ============================= Zset类型 end ============================ */
}
