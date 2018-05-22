package com.example.nettyserver.protocol.fixedheader;

import java.util.Arrays;
import java.util.List;

import com.example.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FixedHeaderDecoder extends ByteToMessageDecoder {
	/**
	 * <pre>
	 *  
	 * 协议开始的标准head_data，占据2个字节. 
	 * 功能位，占据1个字节
	 * 表示数据的长度contentLength，占据4个字节. 
	 * 结束位，占据2个字节
	 * </pre>
	 */
	public final int BASE_LENGTH = 9;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 可读长度必须大于基本长度
		if (in.readableBytes() >= BASE_LENGTH) {
			// 防止socket字节流攻击
			// 防止，客户端传来的数据过大
			// 因为，太大的数据，是不合理的
			if (in.readableBytes() > 1024 * 4) {
				in.skipBytes(in.readableBytes());
			}

			// 记录包头开始的index
			int beginReader;
			int i = 1;
			while (true) {
				// 获取包头开始的index
				beginReader = in.readerIndex();
				// 标记包头开始的index
				in.markReaderIndex();
				// 读到了协议的开始标志，结束while循环
				byte[] head = new byte[2];
				in.readBytes(head);
				String string = StringUtils.byte2HexString(head);
				System.out.println(i + ":" + string);
				i += 1;
				if (Arrays.equals(head, ConstantValue.HEAD_DATA)) {
					break;
				}

				// 未读到包头，略过一个字节
				// 每次略过，一个字节，去读取，包头信息的开始标记
				in.resetReaderIndex();
				in.readByte();

				// 当略过一个字节之后，
				// 数据包的长度，又变得不满足
				// 此时，应该结束。等待后面的数据到达
				if (in.readableBytes() < BASE_LENGTH) {
					return;
				}
			}

			// 功能位
			byte control = in.readByte();
			String str = StringUtils.byte2HexString(new byte[] { control });
			System.out.println("功能位:" + str);

			// 消息的长度
			int length = in.readInt();
			System.out.println("长 度:" + length);

			// 判断请求数据包数据是否到齐
			if (in.readableBytes() < length) {
				// 还原读指针
				in.readerIndex(beginReader);
				return;
			}

			// 读取data数据
			byte[] data = new byte[length];
			in.readBytes(data);

			// 读取结束标识
			byte[] crc = new byte[2];
			in.readBytes(crc);
			String string = StringUtils.byte2HexString(crc);
			System.out.println("结束位:" + string);

			FixedHeaderProtocol protocol = new FixedHeaderProtocol(control, data.length, data);
			out.add(protocol);
		} else if (in.readableBytes() == 1) {
			byte[] b = new byte[1];
			in.readBytes(b);
			byte[] heart = new byte[] { (byte) 0x30 };
			if (Arrays.equals(heart, b)) {
				System.out.println("心跳");
			}
		}

	}

}
