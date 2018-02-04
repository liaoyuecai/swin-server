package com.swin.bean;

import lombok.Data;

@Data
public class MapData implements   MsgData{
    private String tree;
    private String key;
    private byte[] value;
}
