package com.example.nettyserver;

import java.util.concurrent.TimeUnit;

import com.example.nettyserver.protocol.ProtocolType;
import com.example.nettyserver.protocol.fixedheader.FixedHeaderDecoder;
import com.example.nettyserver.protocol.fixedheader.FixedHeaderEncoder;
import com.example.nettyserver.protocol.fixedheader.FixedHeaderServerHandler;
import com.example.nettyserver.protocol.heartbeat.AcceptorIdleStateTrigger;
import com.example.nettyserver.protocol.normal.MyServerHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;

	public ServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		switch (ProtocolType.CURRENT) {
		case ProtocolType.NORMAL:
			// 当作为http请求的时候,需要设置下面两行
			// p.addLast(new HttpServerCodec());/*HTTP 服务的解码器*/
			// p.addLast(new HttpObjectAggregator(2048));/*HTTP 消息的合并处理*/

			p.addLast(new MyServerHandler()); /* 自己写的服务器逻辑处理 */
			break;
		case ProtocolType.FIXHEADER:
			// 设置5s超时
			p.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
			p.addLast(new AcceptorIdleStateTrigger());
			//编、解码器
			p.addLast(new FixedHeaderEncoder());
			p.addLast(new FixedHeaderDecoder());
			// 处理IO
			p.addLast(new FixedHeaderServerHandler());
			break;
		default:
			break;
		}
	}

}
