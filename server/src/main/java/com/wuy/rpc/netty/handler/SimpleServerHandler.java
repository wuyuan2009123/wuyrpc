package com.wuy.rpc.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.wuy.rpc.netty.handler.param.ServerRequest;
import com.wuy.rpc.netty.media.Media;
import com.wuy.rpc.pojo.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try {

			System.out.println("服务器接收到请求........"+msg);
			if ("ping".equals(msg.toString())) {
				return;
			}
			ServerRequest request=null;
			try {
				request = JSONObject.parseObject(msg.toString(),
						ServerRequest.class);
			} catch (Exception e) {
			}
			if(null!=request){
				Media media = Media.newInstance();
				Response result = media.process(request);
				ctx.channel().writeAndFlush(JSONObject.toJSONString(result));
				ctx.channel().writeAndFlush("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state().equals(IdleState.READER_IDLE)) {
				System.out.println("读空闲===");
				ctx.channel().close();
			} else if (event.state().equals(IdleState.WRITER_IDLE)) {
				System.out.println("写空闲=====");
			} else if (event.state().equals(IdleState.ALL_IDLE)) {
				System.out.println("读写空闲");
				ctx.channel().writeAndFlush("ping\r\n");
			}
		}
	}

}
