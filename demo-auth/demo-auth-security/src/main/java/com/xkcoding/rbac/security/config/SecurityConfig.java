package com.xkcoding.rbac.security.config;

import com.xkcoding.rbac.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * <p>
 * Security 配置
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 16:46
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(CustomConfig.class)
public class SecurityConfig {

  @Autowired
  private CustomConfig customConfig;

  @Autowired
  private AccessDeniedHandler accessDeniedHandler;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public BCryptPasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(encoder());
    ProviderManager pm = new ProviderManager(daoAuthenticationProvider);
    return pm;
  }

//  @Override
//  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder());
//  }

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // @formatter:off
        http.cors(Customizer.withDefaults())
            // 关闭 CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 登录行为由自己实现，参考 AuthController#login
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            // 认证请求
            .authorizeHttpRequests( authorize -> {
                authorize
                  // 所有请求都需要登录访问
                  .anyRequest().authenticated();
                  // RBAC 动态 url 认证
                  // .anyRequest().access("@rbacAuthorityService.hasPermission(request,authentication)");
            })
            // 登出行为由自己实现，参考 AuthController#logout
            .logout(AbstractHttpConfigurer::disable)
            // Session 管理 (因为使用了JWT，所以这里不管理Session)
            .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 异常处理
            .exceptionHandling( exception -> {
                // 限制访问异常处理器
                exception.accessDeniedHandler(accessDeniedHandler);
            });
            // @formatter:on

    // 添加自定义 JWT 过滤器
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * 放行所有不需要登录就可以访问的请求，参见 AuthController
   * 也可以在 {@link #configure(HttpSecurity)} 中配置
   * {@code http.authorizeRequests().antMatchers("/api/auth/**").permitAll()}
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> {
      // 忽略 GET
      customConfig.getIgnores().getGet().forEach(url -> web.ignoring().requestMatchers(HttpMethod.GET, url));

      // 忽略 POST
      customConfig.getIgnores().getPost().forEach(url -> web.ignoring().requestMatchers(HttpMethod.POST, url));

      // 忽略 DELETE
      customConfig.getIgnores().getDelete().forEach(url -> web.ignoring().requestMatchers(HttpMethod.DELETE, url));

      // 忽略 PUT
      customConfig.getIgnores().getPut().forEach(url -> web.ignoring().requestMatchers(HttpMethod.PUT, url));

      // 忽略 HEAD
      customConfig.getIgnores().getHead().forEach(url -> web.ignoring().requestMatchers(HttpMethod.HEAD, url));

      // 忽略 PATCH
      customConfig.getIgnores().getPatch().forEach(url -> web.ignoring().requestMatchers(HttpMethod.PATCH, url));

      // 忽略 OPTIONS
      customConfig.getIgnores().getOptions().forEach(url -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, url));

      // 忽略 TRACE
      customConfig.getIgnores().getTrace().forEach(url -> web.ignoring().requestMatchers(HttpMethod.TRACE, url));

      // 按照请求格式忽略
      customConfig.getIgnores().getPattern().forEach(url -> web.ignoring().requestMatchers(url));
    };
  }
}
