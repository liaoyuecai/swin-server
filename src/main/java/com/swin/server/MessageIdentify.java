package com.swin.server;

class MessageIdentify {
    static final int REGISTER_MAP = 0x0000;
    static final int REGISTER_QUEUE = 0x0001;
    static final int SUBSCRIBE_INIT = 0x0100;
    static final int SUBSCRIBE_APPOINT = 0x0101;
    static final int GET_TREE_MAP_DATA = 0x0201;
    static final int GET_TREE_MAP_DATA_OK = 0x0211;
    static final int GET_TREE_MAP_DATA_FAILED = 0x0221;
    static final int PUT_TREE_MAP_DATA = 0x0202;
    static final int PUT_TREE_MAP_DATA_OK = 0x0212;
    static final int PUT_TREE_MAP_DATA_FAILED = 0x0222;
    static final int DELETE_TREE_MAP_DATA = 0x0203;
    static final int DELETE_TREE_MAP_DATA_OK = 0x0213;
    static final int DELETE_TREE_MAP_DATA_FAILED = 0x0233;
    static final int CONNECT_OK = 0x0301;
    static final int CONNECT_EXCEPTION = 0x03FF;
    static final int LISTEN_TREE = 0x0400;
    static final int UN_LISTEN_TREE = 0x040F;
    static final int LISTEN_TREE_UPDATE = 0x0401;
    static final int LISTEN_TREE_DELETE = 0x0402;
    static final int UN_LISTEN_MAP = 0x041F;
    static final int LISTEN_MAP = 0x0410;
    static final int LISTEN_MAP_UPDATE = 0x0411;
    static final int LISTEN_MAP_DELETE = 0x0412;
}
