package com.heima.wemedia.config;

import com.heima.wemedia.interceptor.WmTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//需要保证能被启动类扫描到
/**
 * 拦截器的注册
 *  实现WebMvcConfigurer
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Autowired
//    private WmTokenInterceptor wmTokenInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WmTokenInterceptor())
                .addPathPatterns("/**")  //拦截所有的请求
                .excludePathPatterns("/login/in","/error");
//                .excludePathPatterns(new String[]{"/system/users/login","/system/users/verification"});
    }
}