package com.example.app.pojo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author sgx
 * @Date 2024/3/13 15:24
 * @Description:
 */
@Data
@Slf4j
public class MessageA {
    private String msg;

    private String msgId;

    private String sendTime;

    public MessageA() {
    }

    public MessageA(String msg, String msgId, String sendTime) {
        this.msg = msg;
        this.msgId = msgId;
        this.sendTime = sendTime;
    }
}
