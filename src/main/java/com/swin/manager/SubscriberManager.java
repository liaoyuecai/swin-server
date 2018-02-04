package com.swin.manager;

import com.swin.bean.Consumer;
import com.swin.bean.Listener;
import com.swin.bean.Message;
import com.swin.constant.MessageIdentify;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberManager {
    private static SubscriberManager manager = new SubscriberManager();
    private Map<String, Listener> mapSubscriber;
    private Map<String, Consumer> queueSubscriber;

    private SubscriberManager() {
        this.mapSubscriber = new ConcurrentHashMap<>();
        this.queueSubscriber = new ConcurrentHashMap<>();
    }

    public static SubscriberManager getInstance() {
        return manager;
    }

    public void initReceiver(String clientName, Integer clientType, Channel channel) {
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

    public void disconnect(String clientName, Integer clientType) {
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
