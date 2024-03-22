package com.example.app.service;

/**
 * @Author sgx
 * @Date 2024/3/1 17:29
 * @Description:
 */


import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 自定义WebSocket客户端
 */
@Slf4j
public class CustomizedWebSocketClient extends WebSocketClient {



    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(CustomizedWebSocketClient.class);


    /**
     * 线程安全的Boolean -是否受到消息
     */
    public AtomicBoolean hasMessage = new AtomicBoolean(false);

    /**
     * 线程安全的Boolean -是否已经连接
     */
    private AtomicBoolean hasConnection = new AtomicBoolean(false);

    /**
     * 构造方法
     *
     * @param serverUri
     */
    public CustomizedWebSocketClient(URI serverUri) {
        super(serverUri);
        logger.info("客户端 init:" + serverUri.toString());
    }

    /**
     * 打开连接是方法
     *
     * @param serverHandshake
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("客户端 onOpen");
    }

    /**
     * 收到消息时
     *
     * @param s
     */
    @SneakyThrows
    @Override
    public void onMessage(String s) {
        hasMessage.set(true);
        logger.info("客户端 onMessage:" + s);
        JSONObject object=JSONObject.parseObject(s);
        //String转json，根据key找value
        String sendTime=object.getString("sendTime");


        byte[]  buff = s.getBytes();
        float i = buff.length;
        System.out.println("消息的总字节数"+i);



        String RTT =CustomizedWebSocketClient.RTTCompute(sendTime);
        //System.out.println(RTT);

        float rtt = Float.parseFloat(RTT);


        float deliveryRate=i/rtt;



        String DeliveryRate=Float.toString(deliveryRate);


        System.out.println("传输速率(字节/毫秒)："+DeliveryRate+"   传输时延（毫秒）："+RTT);



        // 处理完毕后，将消息发送给服务器
        sendMessageToServer("传输速率："+DeliveryRate+"   传输时延："+RTT);



    }


    private void sendMessageToServer(String message) {
        // 发送消息给服务器
        this.send(message);
    }

    /**
     * 当连接关闭时
     *
     * @param i
     * @param s
     * @param b
     */
    @Override
    public void onClose(int i, String s, boolean b) {
        this.hasConnection.set(false);
        this.hasMessage.set(false);
        logger.info("客户端 onClose:" + s);
    }

    /**
     * 发生error时
     *
     * @param e
     */
    @Override
    public void onError(Exception e) {
        logger.info("客户端 onError:" + e);
    }

    @Override
    public void connect() {
        if(!this.hasConnection.get()){
            super.connect();
            hasConnection.set(true);
        }
    }




     static String  RTTCompute(String s) throws ParseException{

        // 获取当前时间的毫秒级时间戳
        long currentTimeMillis = System.currentTimeMillis();

        // 将毫秒级时间戳转换为Date对象
        Date currentDate = new Date(currentTimeMillis);

        // 使用SimpleDateFormat对象格式化Date对象
        String  nowTime = sdf.format(currentDate);
        log.info("消息到达客户端时间："+nowTime);
        //System.out.println("发送时间"+s+" ---------------- "+"到达时间"+nowTime);


        Date d2 = sdf.parse(nowTime);
        Date d1 = sdf.parse(s);

        // 计算两个时间之间的差值（单位：毫秒）
        long diffInMilliseconds = d2.getTime() - d1.getTime();


//        URI uri = new URI("ws://localhost:8082/webSocket//12345");
//        CustomizedWebSocketClient webSocketClient = new CustomizedWebSocketClient(uri);
        //webSocketClient.send("时间差（毫秒）：" + diffInMilliseconds);//将时间差发回服务端

        String Rtt=Long.toString(diffInMilliseconds);



        return Rtt;
    }



}

