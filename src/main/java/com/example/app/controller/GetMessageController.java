package com.example.app.controller;

/**
 * @Author sgx
 * @Date 2024/2/29 14:52
 * @Description:
 */

import com.example.app.config.SSEClient;
import com.example.app.service.AmqpReceiver;
import com.example.app.service.CustomizedWebSocketClient;
import com.example.app.util.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/app")
public class GetMessageController {

    @Value("${com.dl.socket.url}")
    private String webSocketUri;

    @Autowired
    private SSEClient sseClient;

    @GetMapping("/webSocket")
    public void abc() throws Exception {
        /**
         * socket连接地址
         */
        /**
         * 注入Socket客户端
         * @return
         */
        URI uri = null;
        try {
            uri = new URI(webSocketUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        CustomizedWebSocketClient webSocketClient = new CustomizedWebSocketClient(uri);
        //启动时创建客户端连接
        webSocketClient.connect();


        //webSocketClient.send();
    }


    @ResponseBody
    @GetMapping("/sse")
    public void sse() {
//        LinkSSE sse=new LinkSSE();
//        sse.linkServer();
        sseClient.subscribeToSSE();

    }


    @ResponseBody
    @GetMapping("/amqpClient")
    public void amqpMessage() throws Exception {

        Runner.runExample(AmqpReceiver.class);

    }

}
