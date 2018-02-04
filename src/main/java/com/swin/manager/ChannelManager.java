package com.swin.manager;

import java.nio.channels.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    private static ChannelManager manager = new ChannelManager();
    private Map<String, Channel> channelMap;

    private ChannelManager() {
        channelMap = new ConcurrentHashMap<>();
    }

    public static ChannelManager getInstance() {
        return manager;
    }

    public void register(String id, Channel channel) {
        channelMap.put(id, channel);
    }

    public void unRegister(String id) {
        channelMap.remove(id);
    }
}
