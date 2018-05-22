package com.example.nettyserver.protocol.normal;

import java.io.UnsupportedEncodingException;

import com.example.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public class MyServerHandler extends ChannelInboundHandlerAdapter {
	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
	private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
	private static final AsciiString CONNECTION = new AsciiString("Connection");
	private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

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
		ByteBuf buf = (ByteBuf) msg;
		byte[] b = new byte[buf.readableBytes()];
		buf.readBytes(b);
		try {
			String body = new String(b, "GBK");
			System.out.println("原始数据:" + StringUtils.byte2HexString(b));
			System.out.println("server:" + body);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		String response = "收到了!";
		try {
			ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes("GBK")));// 解决中文乱码问题

			// 当有写操作时，不需要手动释放msg的引用
			// 当只有读操作时，才需要手动释放msg的引用
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// 当作为http请求的时候，可以用下面的方式解析
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest req = (FullHttpRequest) msg;// 客户端的请求对象

			String strRequest = "收到数据:" + parseJosnRequest(req);
			System.out.println(strRequest);

			// String uri = req.uri();// 获取客户端的URL

			// 根据不同的请求API做不同的处理(路由分发)，只处理POST方法
			if (req.method() == HttpMethod.POST) {

			} else {

			}
			// 向客户端发送结果
			ResponseJson(ctx, req, "{\"ok\":\"" + strRequest + "\"}");
		}
	}

	/**
	 * 响应HTTP的请求
	 * 
	 * @param ctx
	 * @param req
	 * @param jsonStr
	 */
	private void ResponseJson(ChannelHandlerContext ctx, FullHttpRequest req, String jsonStr) {
		boolean keepAlive = HttpUtil.isKeepAlive(req);
		byte[] jsonByteByte = jsonStr.getBytes();
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(jsonByteByte));
		response.headers().set(CONTENT_TYPE, "text/json");
		response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(CONNECTION, KEEP_ALIVE);
			ctx.write(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// cause.printStackTrace();
		System.err.println("出错了.原因: " + cause.getMessage());
		ctx.close();
	}

	/**
	 * 获取请求的内容
	 * 
	 * @param request
	 * @return
	 */
	private String parseJosnRequest(FullHttpRequest request) {
		ByteBuf jsonBuf = request.content();
		String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
		return jsonStr;
	}

}
