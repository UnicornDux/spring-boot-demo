package com.xkcoding.rbac.security.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 默认情况下的模板只能支持RedisTemplate<String, String>，也就是只能存入字符串，因此支持序列化
 * 当我们存入对象的时候，我们需要实现 RedisSerializer 接口，因此在 Redis 序列化其他对象的时候调用
 *
 * @param <T>
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private Class<T> clazz;

  static {
    ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
  }

  public FastJsonRedisSerializer(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public byte[] serialize(T o) throws SerializationException {
    if (o == null) {
      return new byte[0];
    }
    return JSON.toJSONString(o, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
  }

  @Override
  public T deserialize(byte[] bytes) throws SerializationException {
    if (bytes == null || bytes.length < 1) {
      return null;
    }
    String str = new String(bytes, DEFAULT_CHARSET);
    return JSON.parseObject(str, clazz);
  }

}
