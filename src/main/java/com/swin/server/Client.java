package com.swin.server;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * Created by LiaoYuecai on 2018/2/5.
 */
@Data
class Client {

    String id;
    //0: tree;1: queue
    Integer connectType;

    Channel channel;

    boolean alive;

    long disconnectTime;


    public Client(String id, Integer connectType, Channel channel) {
        this.id = id;
        this.connectType = connectType;
        this.channel = channel;
    }

    public void disconnect() {
        this.alive = false;
        this.channel = null;
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
