package com.springboot.blog;

import com.springboot.blog.interceptor.GetRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BlogRestApiWebConfig implements WebMvcConfigurer {

    @Autowired
    GetRequestInterceptor getRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(getRequestInterceptor);
    }
}
