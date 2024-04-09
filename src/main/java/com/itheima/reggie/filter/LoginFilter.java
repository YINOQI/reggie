package com.itheima.reggie.filter;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.itheima.reggie.utils.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {
    private StringRedisTemplate stringRedisTemplate;

    public LoginFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    //    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        //1.获取本次请求的uri
//        String requestURI = request.getRequestURI();
//
//        log.info("拦截到请求:{}",request.getRequestURI());
//
//        //定义不需要处理的请求路径
//        String[] urls = new String[]{
//                "/employee/login",
//                "/employee/logout",
//                "/backend/**",
//                "/front/**",
//                "/common/**",
//                "/user/sendMsg",
//                "/user/login",
////                "/**"
//        };
//
//        //2.判断本次请求是否需要处理
//        boolean check = check(urls, requestURI);
//
//        //3.如果不需要，则直接放行
//        if(check){
//            log.info("本次请求不需要处理:{}",request.getRequestURI());
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        //4.1判断登陆状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("employee") != null){
//            Long empId = (Long) request.getSession().getAttribute("employee");
//            log.info("用户已登录，id为:{}",empId);
//
//            BaseContext.setCurrentId(empId);
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        //4.2判断登陆状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("user") != null){
//            Long userId = (Long) request.getSession().getAttribute("user");
//            log.info("用户已登录，id为:{}",userId);
//
//            BaseContext.setCurrentId(userId);
//            filterChain.doFilter(request,response);
//            return;
//        }
//        //5.如果未登录则返回未登录结果
//        log.info("用户未登录...");
//        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = request.getHeader("authorization");

        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
//                "/**"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        if (StrUtil.isNotBlank(token)) {
            // 基于token获取redis中的用户
            String key = RedisConstants.LOGIN_USER_KEY + token;
            Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

            // 判断登陆状态，如果已登录，则直接放行
            if (!userMap.isEmpty()) {
                log.info("用户已登录，id为:{}", userMap.get("id"));

                String id = (String)userMap.get("id");
                BaseContext.setCurrentId(Long.parseLong(id));

                stringRedisTemplate.expire(key,30L, TimeUnit.MINUTES);

                filterChain.doFilter(request, response);
                return;
            }
        }

        log.info("用户未登录...");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url :
                urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
