package com.springboot.blog.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;

@ControllerAdvice
public class RequestBodyInterceptor extends RequestBodyAdviceAdapter {

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    @SneakyThrows
    @Override
    public Object afterBodyRead(Object body,
                                HttpInputMessage inputMessage,
                                MethodParameter parameter,
                                Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        ObjectMapper mapper = new ObjectMapper();

        LOGGER.info("Method: "+ httpServletRequest.getMethod() + " --- " +
                "Path: "+ httpServletRequest.getRequestURI() + " --- " +
                "Header: "+ httpServletRequest.getHeaderNames() + " --- " +
                "Request Body: " + mapper.writeValueAsString(body));

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
