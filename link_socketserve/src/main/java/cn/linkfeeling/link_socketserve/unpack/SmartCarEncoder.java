package cn.linkfeeling.link_socketserve.unpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SmartCarEncoder extends MessageToByteEncoder<SmartCarProtocol> {

	@Override
	protected void encode(ChannelHandlerContext tcx, SmartCarProtocol msg,
						  ByteBuf out) throws Exception {
		// 写入消息SmartCar的具体内容
		// 1.写入消息的开头的信息标志(int类型)
		out.writeShort(msg.getHead_data());
		// 2.写入消息的长度(int 类型)
		out.writeByte(msg.getContentLength());
		// 3.写入消息的内容(byte[]类型)
		out.writeBytes(msg.getContent());
	}
}
