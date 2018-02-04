package com.swin.bean;

import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Receiver implements Serializable {
    public static final int CONSUMER = 0;
    public static final int LISTENER = 1;
    protected String name;
    protected Channel channel;
    protected boolean alive;
    protected long disconnectTime;


    public Receiver(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
        this.alive = true;
    }

    public void disconnect() {
        this.alive = false;
        this.disconnectTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return this.alive;
    }

    public long disconnectTime(long time) {
        if (this.alive) {
            return -1;
        } else {
            return time - this.disconnectTime;
        }
    }

}
