package com.xkcoding.rbac.security.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisSerializeConfig {

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
     RedisTemplate<Object, Object> template = new RedisTemplate<>();
     template.setConnectionFactory(connectionFactory);
     FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Object.class);
     // 使用 StringRedisSerializer 序列化 redis 的 key 值
     template.setKeySerializer(new StringRedisSerializer());
     template.setValueSerializer(serializer);

     // Hash 格式的存储的序列化方式
     template.setHashKeySerializer(new StringRedisSerializer());
     template.setHashValueSerializer(serializer);

     template.afterPropertiesSet();

     return template;
  }
}
