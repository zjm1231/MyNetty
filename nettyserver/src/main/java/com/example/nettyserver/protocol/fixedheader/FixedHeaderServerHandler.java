package com.example.nettyserver.protocol.fixedheader;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import com.example.utils.StringUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FixedHeaderServerHandler extends ChannelInboundHandlerAdapter {
	// ChannelRegistered() 注册连接
	// ChannelUnregistered() 取消注册
	// ChannelActive() 连接激活
	// ChannelRead(Object) 读取数据
	// ChannelReadComplete() 读取完成
	// ExceptionCaught(Throwable) 发生异常
	// UserEventTriggered(Object) 用户事件触发
	// ChannelWritabilityChanged() 可写状态改变
	// ChannelInactive() 检测到连接断开

	// 客户端连上的时候触发
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// super.channelRegistered(ctx);
		String response = "Hi Client!!!";
		ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
		System.out.println("注册连接 " + ctx.channel().id());
	}

	// 连接断开的时候
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("取消注册 " + ctx.channel().id());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接激活 " + ctx.channel().id());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接断开 " + ctx.channel().id());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	// 数据读取
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		FixedHeaderProtocol fix = (FixedHeaderProtocol) msg;

		System.out.println("内容部分原始数据:" + StringUtils.byte2HexString(fix.getContent()));
		// 解决中文乱码问题
		Charset charset = Charset.defaultCharset();
		ByteBuffer buf = ByteBuffer.wrap(fix.getContent());
		CharBuffer cBuf = charset.decode(buf);
		System.out.println("转换为字符串数据:" + cBuf.toString());

		// 收到数据后，回复客户端
		String str = "收到了.";
		byte control = fix.getControl();
		FixedHeaderProtocol response = new FixedHeaderProtocol(control, str.getBytes().length, str.getBytes());
		// 当服务端完成写操作后，关闭与客户端的连接
		ctx.writeAndFlush(response);
		// .addListener(ChannelFutureListener.CLOSE);

		// 当有写操作时，不需要手动释放msg的引用
		// 当只有读操作时，才需要手动释放msg的引用
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.err.println("出错了.原因: " + cause.getMessage());
		ctx.close();
	}

}
