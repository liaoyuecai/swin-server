package com.swin.bean;


import io.netty.channel.Channel;

public class Consumer extends Receiver {
    public Consumer(String name, Channel channel) {
        super(name, channel);
    }
}
