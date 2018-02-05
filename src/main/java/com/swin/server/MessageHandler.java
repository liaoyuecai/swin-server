package com.swin.server;

import com.swin.bean.MapData;
import com.swin.bean.Message;
import com.swin.utils.CoderUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = LoggerFactory.getLogger(MapDBFactory.class);

    String clientId;

    Integer clientType;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ClientManager.disconnect(clientId);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Integer identify = message.getIdentify();
        switch (identify) {
            case MessageIdentify.REGISTER_MAP:
            case MessageIdentify.REGISTER_QUEUE:
                if (this.clientId == null) {
                    this.clientId = message.getClientId();
                    this.clientType = identify;
                    ctx.channel().writeAndFlush(ClientManager.register(clientId, clientType, ctx.channel()));
                }
                break;
            case MessageIdentify.PUT_TREE_MAP_DATA:
                ctx.channel().writeAndFlush(putTreeMapData(message));
                break;
            case MessageIdentify.GET_TREE_MAP_DATA:
                ctx.channel().writeAndFlush(getTreeMapData(message));
                break;
            case MessageIdentify.DELETE_TREE_MAP_DATA:
                ctx.channel().writeAndFlush(deleteTreeMapData(message));
                break;
            case MessageIdentify.SUBSCRIBE_INIT:

                break;
        }
    }

    private Object deleteTreeMapData(Message message) {
        MapData data = (MapData) message.getData();
        try {
            MapDBFactory.remove(data.getTree(), data.getKey());
            message.setIdentify(MessageIdentify.DELETE_TREE_MAP_DATA_OK);
            data.setValue(null);
        } catch (Exception e) {
            message.setIdentify(MessageIdentify.DELETE_TREE_MAP_DATA_FAILED);
            data.setValue(CoderUtils.getBytes("Delete message exception, " + e.getMessage()));
        }
        return message;
    }

    private Message putTreeMapData(Message message) {
        MapData data = (MapData) message.getData();
        try {
            MapDBFactory.addOrUpdate(data.getTree(), data.getKey(), data.getValue());
            message.setIdentify(MessageIdentify.PUT_TREE_MAP_DATA_OK);
            data.setValue(null);
        } catch (Exception e) {
            message.setIdentify(MessageIdentify.PUT_TREE_MAP_DATA_FAILED);
            data.setValue(CoderUtils.getBytes("Put message exception, " + e.getMessage()));
        }
        return message;
    }

    private Message getTreeMapData(Message message) {
        MapData data = (MapData) message.getData();
        try {
            data.setValue(MapDBFactory.getDataByTreeAndKey(data.getTree(), data.getKey()));
            message.setIdentify(MessageIdentify.GET_TREE_MAP_DATA_OK);
        } catch (Exception e) {
            message.setIdentify(MessageIdentify.GET_TREE_MAP_DATA_FAILED);
            data.setValue(CoderUtils.getBytes("Get message exception, " + e.getMessage()));
        }
        return message;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.error("Client :" + clientId + " timeout , client is closing");
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
