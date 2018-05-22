package com.example.nettyserver.protocol.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {
	/**
	 * 超时次数，如果超过一定次数，就认为客户端已经掉线，关闭连接。
	 */
	private int loss_connect_time = 0;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				loss_connect_time++;
				System.out.println("5 秒没有接收到客户端的信息了");
				if (loss_connect_time > 2) {
					System.out.println("关闭channel -> " + ctx.channel().id());
					ctx.channel().close();
				}
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

}
