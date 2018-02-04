package com.swin.server.netty.factory;

import com.swin.bean.MapData;
import com.swin.bean.Message;
import com.swin.constant.MessageIdentify;
import com.swin.utils.CoderUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {
        if (o instanceof Message) {
            Message message = (Message) o;
            ByteBuf buf = Unpooled.buffer();
            Integer len = 0;
            buf.writeInt(len);
            Integer identify = message.getIdentify();
            buf.writeInt(identify);
            len += 4;
            len += CoderUtils.writeStrAndLen16(buf, message.getUuid());
            len += 2;
            len += CoderUtils.writeStrAndLen16(buf, message.getClientId());
            len += 2;
            switch (identify) {
                case MessageIdentify.REGISTER_MAP:
                case MessageIdentify.GET_TREE_MAP_DATA_OK:
                case MessageIdentify.GET_TREE_MAP_DATA_FAILED:
                case MessageIdentify.PUT_TREE_MAP_DATA_OK:
                case MessageIdentify.PUT_TREE_MAP_DATA_FAILED:
                    MapData data = (MapData) message.getData();
                    if (data != null) {
                        len += CoderUtils.writeStrAndLen16(buf, data.getTree());
                        len += 2;
                        len += CoderUtils.writeStrAndLen16(buf, data.getKey());
                        len += 2;
                        len += CoderUtils.writeBytesAndLen32(buf, data.getValue());
                        len += 4;
                    }
                    break;
            }
            buf.writerIndex(0);
            buf.writeInt(len);
            buf.writerIndex(len + 4);
            out.writeBytes(buf);
            buf.release();
        }
    }
}
