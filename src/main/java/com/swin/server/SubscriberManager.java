package com.swin.server;

import com.swin.bean.Consumer;
import com.swin.bean.Listener;
import com.swin.bean.Message;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SubscriberManager {
    static Map<String, Listener> mapSubscriber;
    static Map<String, Consumer> queueSubscriber;

    static {
        mapSubscriber = new ConcurrentHashMap<>();
        queueSubscriber = new ConcurrentHashMap<>();
    }

    static void initReceiver(String clientName, Integer clientType, Channel channel) {
        Message message = new Message();
        switch (clientType) {
            case 0:
                if (mapSubscriber.containsKey(clientName) && mapSubscriber.get(clientName).isAlive()) {
                    message.setIdentify(MessageIdentify.CONNECT_EXCEPTION);
                    message.setClientId("The clientId has be used in MAP , please use the other one");
                } else {
                    message.setIdentify(MessageIdentify.CONNECT_OK);
                    mapSubscriber.put(clientName, new Listener(clientName, channel));
                }
                break;
            case 1:
                if (queueSubscriber.containsKey(clientName) && queueSubscriber.get(clientName).isAlive()) {
                    message.setIdentify(MessageIdentify.CONNECT_EXCEPTION);
                    message.setClientId("The clientId has be used in QUEUE , please use the other one");
                } else {
                    message.setIdentify(MessageIdentify.CONNECT_OK);
                    queueSubscriber.put(clientName, new Consumer(clientName, channel));
                }
                break;
        }
        channel.writeAndFlush(message);
    }

    static void disconnect(String clientName, Integer clientType) {
        switch (clientType) {
            case 0:
                mapSubscriber.get(clientName).disconnect();
                break;
            case 1:
                queueSubscriber.get(clientName).disconnect();
                break;
        }
    }

}
