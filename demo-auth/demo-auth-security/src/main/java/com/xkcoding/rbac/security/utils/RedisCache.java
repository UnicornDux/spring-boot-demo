package com.xkcoding.rbac.security.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
@Component
public class RedisCache {

  @Autowired
  public RedisTemplate redisTemplate;

  /**
   * 缓存基本的对象， Integer, String, 实体类等
   *
   * @param key
   * @param value
   * @param <T>
   */
  public <T> void setCacheObject(final String key, final T value) {
    redisTemplate.opsForValue().set(key, value);
  }


  /**
   * 缓存基本的对象， Integer, String, 实体类等
   *
   * @param key      : 缓存的键
   * @param value    : 缓存的值
   * @param timeout  : 超时时间
   * @param timeUnit : 超时时间的单位
   * @param <T>
   */
  public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
    redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
  }

  /**
   * 设置有效时间
   *
   * @param key     : 设置 key
   * @param timeout : 超时时间
   * @return true=设置成功, false=设置失败
   */
  public boolean expire(final String key, final long timeout) {
    return expire(key, timeout, TimeUnit.SECONDS);
  }

  /**
   * 设置有效时间
   *
   * @param key      : 设置的 key
   * @param timeout  : 设置的超时时间
   * @param timeUnit : 设置的时间单位
   * @return true=设置成功， false=设置失败
   */
  public boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
    return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
  }

  /**
   * 获取缓存的基本对象
   *
   * @param key : 获取值的 key
   * @param <T>
   * @return : 缓存键对应的值
   */
  public <T> T getCacheObject(final String key) {
    ValueOperations<String, T> operations = redisTemplate.opsForValue();
    return operations.get(key);
  }

  /**
   * 删除单个对象
   *
   * @param key : 删除对象的 key
   * @return
   */
  public boolean deleteObject(final String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }


  /**
   * 删除集合对象
   *
   * @param collection 多个对象
   * @return
   */
  public long deleteObject(final Collection collection) {
    return redisTemplate.delete(collection);
  }

  /**
   * 缓存 List 数据
   *
   * @param key      : 缓存数据的 key
   * @param dataList : 待存储的数据
   * @param <T>
   * @return 缓存的数据的数量
   */
  public <T> long setCacheList(final String key, final List<T> dataList) {
    Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
    return count == null ? 0 : count;
  }

  /**
   * 获取缓存的 list 对象
   *
   * @param key
   * @param <T>
   * @return
   */
  public <T> List<T> getCacheList(final String key) {
    return redisTemplate.opsForList().range(key, 0, -1);
  }

  /**
   * 缓存 set
   *
   * @param key     : 缓存键盘
   * @param dataSet : 缓存的数据
   * @param <T>
   * @return
   */
  public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
    BoundSetOperations<String, T> setOperations = redisTemplate.boundSetOps(key);
    Iterator<T> it = dataSet.iterator();
    while (it.hasNext()) {
      setOperations.add(it.next());
    }
    return setOperations;
  }

  /**
   * 获取缓存的 set 数据
   *
   * @param key : 缓存数据的 key
   * @param <T>
   * @return
   */
  public <T> Set<T> getCacheSet(final String key) {
    return redisTemplate.opsForSet().members(key);
  }

  /**
   * 缓存 map
   *
   * @param key     : 缓存键
   * @param dataMap : 缓存的数据
   * @param <T>
   */
  public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
    if (dataMap != null) {
      redisTemplate.opsForHash().putAll(key, dataMap);
    }
  }

  /**
   * 获取缓存的 Map
   *
   * @param key : 获取数据的 key
   * @param <T>
   * @return
   */
  public <T> Map<String, T> getCacheMap(final String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  public <T> void setCacheMapValue(final String key, final String hkey, final T value) {
    redisTemplate.opsForHash().put(key, hkey, value);
  }

  /**
   * 获取 hash 中的数据
   *
   * @param key  : 缓存 key
   * @param hkey : Hash 数据中的 field
   * @param <T>
   * @return
   */
  public <T> T getCacheMapValue(final String key, final String hkey) {
    HashOperations<String, String, T> operations = redisTemplate.opsForHash();
    return operations.get(key, hkey);
  }

  /**
   * 删除 hash 中的数据
   *
   * @param key
   * @param hkey
   */
  public void delCacheMapValue(final String key, final String hkey) {
    HashOperations hashOperations = redisTemplate.opsForHash();
    hashOperations.delete(key, hkey);
  }

  /**
   * 取 hash 中多个 field 中的值
   *
   * @param key
   * @param hkeys
   * @param <T>
   * @return
   */
  public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hkeys) {
    return redisTemplate.opsForHash().multiGet(key, hkeys);
  }


  /**
   * 获取缓存的基本对象列表
   *
   * @param pattern
   * @return
   */
  public Collection<String> keys(final String pattern) {
    return redisTemplate.keys(pattern);
  }

}
