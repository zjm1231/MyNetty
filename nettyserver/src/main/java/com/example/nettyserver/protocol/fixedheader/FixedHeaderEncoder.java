package com.example.nettyserver.protocol.fixedheader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <pre>
 *  
 * 自定义的协议 
 *  数据包格式 
 * +——--------——+——---------——+——---------——+——---------——+——--------——+ 
 * | 协议开始标识|    功能位      |   数据长度     |   数据内容     |  结束标识     |
 * +——--------——+——---------——+——---------——+——---------——+——--------——+ 
 * 1.协议开始标志head_data，16进制表示为0xAA,0x55  【2字节】
 * 2.功能位  【1字节】
 * 2.传输数据的长度contentLength  【4字节】
 * 2.传输数据的内容
 * 3.结束标识，2字节，16进制表示为0X1C,0X1D  【2字节】
 * </pre>
 */
public class FixedHeaderEncoder extends MessageToByteEncoder<FixedHeaderProtocol> {
	@Override
	protected void encode(ChannelHandlerContext ctx, FixedHeaderProtocol msg, ByteBuf out) throws Exception {
		// 1消息的开始标识
		out.writeBytes(msg.getHead_data());
		// 2功能位
		out.writeByte(msg.getControl());
		// 3消息的长度
		out.writeInt(msg.getContentLength());
		// 4消息的内容
		out.writeBytes(msg.getContent());
		// 5消息结束标识
		out.writeBytes(msg.getCrc_data());
	}

}
