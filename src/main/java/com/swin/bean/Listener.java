package com.swin.bean;


import io.netty.channel.Channel;
import lombok.Data;


@Data
public class Listener extends Receiver {
    public Listener(String name, Channel channel) {
        super(name, channel);
    }
}
