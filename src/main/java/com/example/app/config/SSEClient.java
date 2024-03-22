package com.example.app.config;


import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;


import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @Author sgx
 * @Date 2024/3/8 20:04
 * @Description:
 */
@Component
public class SSEClient {


    public void subscribeToSSE() {


        //这三行代码是修改SSE接受消息的中文字符集，否则会乱码
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        //消息转换器列表
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        //配置消息转换器StringHttpMessageConverter，并设置utf-8
        messageConverters.set(1,
                new StringHttpMessageConverter(StandardCharsets.UTF_8));//支持中文字符集，默认ISO-8859-1，支持utf-8

        String sseUrlA = "http://localhost:8083/mall/rabbitmq/singleThreadSsePush";

        String respA = String.valueOf(restTemplate.getForEntity(sseUrlA, String.class));
        System.out.println(respA);


    }


}