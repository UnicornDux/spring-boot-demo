package com.xkcoding.rbac.security.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

  // 设置 token 有效期
  public static final Long JWT_TTL = 60 * 60 * 1000L; // 一小时

  // 设置密钥铭文
  public static final String JWT_KEY = "unicorn1122334455667788990012138";

  /**
   * 密钥加密
   */
  public static final SecretKey KEY = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

  /**
   * 指定算法
   *
   * @return
   */

  private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;


  public static String getUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }


  // 生成 jwt
  public static String createJWT(String subject) {
    JwtBuilder builder = getJwtBuilder(subject, null, getUUID()); // 设置过期时间
    return builder.compact();
  }

  /**
   * 生成 JWT
   *
   * @param subject : token 中需要存放的数据，已经序列化为 json 格式
   * @param ttl     : token 的超时时间
   * @return
   */
  public static String createJWT(String subject, Long ttl) {
    JwtBuilder builder = getJwtBuilder(subject, ttl, getUUID());
    return builder.compact();
  }

  public static String createJWT(String id, String subject, Long ttl) {
    JwtBuilder builder = getJwtBuilder(subject, ttl, id);
    return builder.compact();
  }

  private static JwtBuilder getJwtBuilder(String subject, Long ttl, String uuid) {
    SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;
    SecretKey secretKey = generalKey();
    long nowMillis = System.currentTimeMillis();
    Date now = new Date(nowMillis);
    if (ttl == null) {
      ttl = JwtUtil.JWT_TTL;
    }
    long expMillis = nowMillis + ttl;
    Date expireDate = new Date(expMillis);

    return Jwts.builder()
      .id(uuid) // 唯一ID
      .subject(subject) // 主题，可以是 Json 数据
      .issuer("sg") // 签发者
      .issuedAt(now)
      .signWith(KEY, ALGORITHM) // 使用 HS256 对称加密算法签名，第二个参数为密钥
      .expiration(expireDate);
  }

  public static SecretKey generalKey() {
    byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
    return new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");
  }

  public static Claims parseJWT(String jwt) throws Exception {
    return Jwts.parser().decryptWith(KEY).build().parseSignedClaims(jwt).getPayload();
  }
}
