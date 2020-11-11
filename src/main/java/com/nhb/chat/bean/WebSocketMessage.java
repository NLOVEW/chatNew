package com.nhb.chat.bean;

import java.io.Serializable;

/**
 * @author luck_nhb
 * @version 1.0
 * @description
 * @date 2020/11/10 17:22
 */
public class WebSocketMessage implements Serializable {
    private String sender;
    private String receiver;
    private String content;
    private String sendTime;
    private String ip;
    private String city;
    private String type;

    public WebSocketMessage() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WebSocketMessage(String sender, String receiver, String content, String sendTime, String ip, String city, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sendTime = sendTime;
        this.ip = ip;
        this.city = city;
        this.type = type;
    }

    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", ip='" + ip + '\'' +
                ", city='" + city + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
