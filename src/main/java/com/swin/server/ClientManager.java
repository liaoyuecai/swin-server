package com.swin.server;

import com.swin.bean.MapData;
import com.swin.bean.Message;
import io.netty.channel.Channel;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LiaoYuecai on 2018/2/2.
 */
class ClientManager {

    public enum LISTEN_TYPE {
        TREE, MAP
    }

    public enum LISTEN_STATUS {
        OPEN, CLOSE
    }

    public enum LISTEN_OPERATE {
        UPDATE(1), DELETE(2);
        final int value;

        private LISTEN_OPERATE(int value) {
            this.value = value;
        }

    }

    private static Map<String, Client> clientMap;
    private static Map<String, Set<String>> treeListen;
    private static Map<String, Set<String>> mapListen;

    static {
        clientMap = new ConcurrentHashMap<>();
        treeListen = new ConcurrentHashMap<>();
        mapListen = new ConcurrentHashMap<>();
    }


    static Message register(String clientId, Integer clientType, Channel channel) {
        Message message = new Message();
        if (clientMap.containsKey(clientId) && clientMap.get(clientId).isAlive()) {
            message.setIdentify(MessageIdentify.CONNECT_EXCEPTION);
            message.setClientId("The clientId has be used , please use the other one");
            return message;
        }
        message.setIdentify(MessageIdentify.CONNECT_OK);
        clientMap.put(clientId, new Client(clientId, clientType, channel));
        return message;
    }

    static void unRegister(@NotNull String clientId) {
        clientMap.remove(clientId);
    }

    static void listen(LISTEN_TYPE type, LISTEN_STATUS status, String key, String clientId) {
        switch (type) {
            case TREE:
                listenMap(status, key, clientId, treeListen);
                break;
            case MAP:
                listenMap(status, key, clientId, mapListen);
                break;
        }
    }

    private static void listenMap(LISTEN_STATUS status, String key, String clientId, Map<String, Set<String>> listen) {
        switch (status) {
            case OPEN:
                if (!listen.containsKey(key)) {
                    listen.put(key, new HashSet<>());
                }
                listen.get(key).add(clientId);
                break;
            case CLOSE:
                if (listen.containsKey(key)) {
                    Set<String> set = listen.get(key);
                    set.remove(clientId);
                    if (set.isEmpty()) {
                        listen.remove(key);
                    }
                }
                break;
        }
    }

    static void informTree(String tree, LISTEN_OPERATE operate, String key, byte[] value) {
        boolean treeFlag = treeListen.containsKey(tree);
        boolean mapFlag = mapListen.containsKey(tree);
        if (!treeFlag && !mapFlag) {
            return;
        }
        Message message = new Message();
        MapData data = new MapData();
        data.setTree(tree);
        data.setKey(key);
        data.setValue(value);
        message.setData(data);
        if (treeFlag) {
            message.setIdentify(MessageIdentify.LISTEN_TREE + operate.value);
            for (String clientId : treeListen.get(tree)) {
                message.setClientId(clientId);
                clientMap.get(clientId).getChannel().writeAndFlush(message);
            }
        }
        if (mapFlag) {
            message.setIdentify(MessageIdentify.LISTEN_MAP + operate.value);
            for (String clientId : mapListen.get(tree)) {
                message.setClientId(clientId);
                clientMap.get(clientId).getChannel().writeAndFlush(message);
            }
        }
    }

    static void disconnect(String clientId) {
        if (clientMap.containsKey(clientId)) {
            clientMap.get(clientId).disconnect();
        }
    }

}

