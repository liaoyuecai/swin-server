package com.swin.server.netty.factory;

import com.swin.bean.MapData;
import com.swin.bean.Message;
import com.swin.constant.MessageIdentify;
import com.swin.db.MapDBFactory;
import com.swin.manager.SubscriberManager;
import com.swin.utils.CoderUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private String clientId;

    private Integer clientType;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        SubscriberManager.getInstance().disconnect(clientId, clientType);
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
                    SubscriberManager.getInstance().initReceiver(clientId, clientType, ctx.channel());
                }
                break;
            case MessageIdentify.PUT_TREE_MAP_DATA:
                long a = System.currentTimeMillis();
                ctx.channel().writeAndFlush(putTreeMapData(message.getUuid(), (MapData) message.getData()));
                long b = System.currentTimeMillis() -a ;
                break;
            case MessageIdentify.GET_TREE_MAP_DATA:
                ctx.channel().writeAndFlush(getTreeMapData(message.getUuid(), (MapData) message.getData()));
                break;
            case MessageIdentify.SUBSCRIBE_INIT:

                break;
        }
    }

    private Message putTreeMapData(String id, MapData data) {
        Message message = new Message();
        message.setClientId(clientId);
        message.setUuid(id);
        try {
            MapDBFactory.addOrUpdate(data.getTree(), data.getKey(), data.getValue());
            message.setIdentify(MessageIdentify.PUT_TREE_MAP_DATA_OK);
            data.setValue(null);
        } catch (Exception e) {
            message.setIdentify(MessageIdentify.PUT_TREE_MAP_DATA_FAILED);
            data.setValue(CoderUtils.getBytes("Put message exception, " + e.getMessage()));
        }
        message.setData(data);
        return message;
    }

    private Message getTreeMapData(String id, MapData data) {
        Message message = new Message();
        message.setClientId(clientId);
        message.setUuid(id);
        try {
            data.setValue(MapDBFactory.getDataByTreeAndKey(data.getTree(), data.getKey()));
            message.setIdentify(MessageIdentify.GET_TREE_MAP_DATA_OK);
        } catch (Exception e) {
            message.setIdentify(MessageIdentify.GET_TREE_MAP_DATA_FAILED);
            data.setValue(CoderUtils.getBytes("Get message exception, " + e.getMessage()));
        }
        message.setData(data);
        return message;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
