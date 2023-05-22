package com.springboot.blog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.invoke.MethodHandles;

@Component
public class GetRequestInterceptor implements HandlerInterceptor {

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // Log GET Request
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name()))
        {
            LOGGER.info("Method: "+ httpServletRequest.getMethod() + " --- " +
                    "Path: "+ httpServletRequest.getRequestURI() + " --- " +
                    "Header: "+ httpServletRequest.getHeaderNames());
        }

        return true;
    }


}
