package com.example.app.service;

/**
 * @Author sgx
 * @Date 2024/3/17 16:27
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;

import io.vertx.proton.ProtonSender;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.messaging.Accepted;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;

import org.apache.qpid.proton.message.Message;


import io.vertx.core.AbstractVerticle;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AmqpReceiver extends AbstractVerticle {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private String address = "examples";


    @Override
    public void start() throws Exception {
        ProtonClient client = ProtonClient.create(vertx);

        client.connect("localhost", 5673, res -> {
            if (!res.succeeded()) {
                System.out.println("Connect failed: " + res.cause());
                return;
            }

            ProtonConnection connection = res.result();
            connection.open();

            connection.createReceiver(address).handler((delivery, msg) -> {
                String content = (String) ((AmqpValue) msg.getBody()).getValue();

                System.out.println("Received message with content: " + content);

                //-----------------------------------------------------------------
                //转JSON
                JSONObject object = JSONObject.parseObject(content);
                //String转json，根据key找value
                String sendTime = object.getString("sendTime");

                byte[] buff = content.getBytes();
                float i = buff.length;
                System.out.println("消息的总字节数" + i);

                String RTT = null;
                try {
                    RTT = CustomizedWebSocketClient.RTTCompute(sendTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //System.out.println(RTT);


                assert RTT != null;

                float rtt = Float.parseFloat(RTT);
                float deliveryRate = i / rtt;

                //String DeliveryRate = Float.toString(deliveryRate);
                double intervalTime=i/(2.8*deliveryRate);

                String gapTime=Double.toString(intervalTime);

                System.out.println("间隔时间:  "+gapTime);


                //System.out.println("传输速率(字节/毫秒)：" + DeliveryRate + "   传输时延（毫秒）：" + RTT);

                //-----------------------------------------------------------------------
                String Content = "处理后返回给服务器的消息：传输速率(字节/毫秒)：" +deliveryRate + "   传输时延（毫秒）：" + RTT;
                Message processedMessage = Proton.message();
                processedMessage.setBody(new AmqpValue(Content));
                // 发送处理后的消息给服务器
                // 设置消息的目标地址
                processedMessage.setAddress(address);

                // 创建一个 sender 来发送处理后的消息
                ProtonSender sender = connection.createSender(null);
                sender.open();
                sender.send(processedMessage);
                //-----------------------------------------------------------------------

                // 手动接受消息并进行结算
                delivery.disposition(Accepted.getInstance(), true);

                // 关闭 sender
                sender.close();


            }).open();
        });
    }
}
