package com.swin.server.netty.factory;

import com.swin.bean.MapData;
import com.swin.bean.Message;
import com.swin.constant.MessageIdentify;
import com.swin.utils.CoderUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Map;

public class MessageDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        Message message = new Message();
        Integer identify = CoderUtils.getInt(buf);
        message.setIdentify(identify);
        Integer idLen = CoderUtils.getShort(buf);
        if (idLen > 0) {
            message.setUuid(CoderUtils.getString(buf, idLen));
        }
        Integer clientIdLen = CoderUtils.getShort(buf);
        if (clientIdLen > 0) {
            message.setClientId(CoderUtils.getString(buf, clientIdLen));
        }
        switch (identify) {
            case MessageIdentify.GET_TREE_MAP_DATA:
            case MessageIdentify.PUT_TREE_MAP_DATA:
                MapData data = new MapData();
                Integer treeLen = CoderUtils.getShort(buf);
                data.setTree(CoderUtils.getString(buf, treeLen));
                Integer keyLen = CoderUtils.getShort(buf);
                data.setKey(CoderUtils.getString(buf, keyLen));
                Integer valueLen = CoderUtils.getInt(buf);
                if (valueLen > 0) {
                    data.setValue(CoderUtils.getBytes(buf, valueLen));
                }
                message.setData(data);
                break;
        }
        list.add(message);
    }

}
