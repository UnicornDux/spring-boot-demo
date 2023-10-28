package com.xkcoding.rbac.security.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Sets;
import com.xkcoding.rbac.security.common.Status;
import com.xkcoding.rbac.security.exception.SecurityException;
import com.xkcoding.rbac.security.service.CustomUserDetailsService;
import com.xkcoding.rbac.security.util.JwtUtil;
import com.xkcoding.rbac.security.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Set;

import static org.springframework.http.HttpMethod.*;

/**
 * <p>
 * Jwt 认证过滤器
 * ----------------------------------------------------------------------
 * > 1. 拦截请求，当我们的请求中携带了 token, 尽心 token 认证，
 *      并在 SecurityContextHolder, redis 中做存储
 * > 2. 当请求中没有 token 则直接拦截，不允许访问资源，要求必须先认证
 * ----------------------------------------------------------------------
 *  这里实现的是 Spring 为我们提供一个过滤器，保证了一个请求只会进入这个过滤器一次
 *  (在不同的 servlet 容器的实现里，有的容器实现会进入多次)
 *
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 15:15
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomConfig customConfig;

    @Override
    protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException, ServletException {

        if (checkIgnores(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = jwtUtil.getJwtFromRequest(request);

        if (StrUtil.isNotBlank(jwt)) {
            try {
                String username = jwtUtil.getUsernameFromJWT(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch (SecurityException e) {
                ResponseUtil.renderJson(response, e);
            }
        } else {
            ResponseUtil.renderJson(response, Status.UNAUTHORIZED, null);
        }
    }

    /**
     * 请求是否不需要进行权限拦截
     *
     * @param request 当前请求
     * @return true - 忽略，false - 不忽略
     */
    private boolean checkIgnores(HttpServletRequest request) {
        String method = request.getMethod();
        if (StringUtils.isEmpty(method)) {
            method = "GET";
        }

        Set<String> ignores = Sets.newHashSet();

        switch (method) {
          case "GET":
                ignores.addAll(customConfig.getIgnores().getGet());
                break;
            case "PUT":
                ignores.addAll(customConfig.getIgnores().getPut());
                break;
            case "HEAD":
                ignores.addAll(customConfig.getIgnores().getHead());
                break;
            case "POST":
                ignores.addAll(customConfig.getIgnores().getPost());
                break;
            case "PATCH":
                ignores.addAll(customConfig.getIgnores().getPatch());
                break;
            case "TRACE":
                ignores.addAll(customConfig.getIgnores().getTrace());
                break;
            case "DELETE":
                ignores.addAll(customConfig.getIgnores().getDelete());
                break;
            case "OPTIONS":
                ignores.addAll(customConfig.getIgnores().getOptions());
                break;
            default:
                break;
        }

        ignores.addAll(customConfig.getIgnores().getPattern());

        if (CollUtil.isNotEmpty(ignores)) {
            for (String ignore : ignores) {
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(ignore, method);
                if (matcher.matches(request)) {
                    return true;
                }
            }
        }
        return false;
    }
}
