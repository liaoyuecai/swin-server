package com.swin.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Message implements Serializable {
    protected Integer identify;
    protected String clientId;
    protected String uuid = UUID.randomUUID().toString();
    protected MsgData data;
}
