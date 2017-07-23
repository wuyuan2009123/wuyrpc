package com.wuy.rpc.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import com.alibaba.fastjson.JSONObject;
import com.wuy.rpc.client.factory.ZookeeperFactory;
import com.wuy.rpc.client.handler.SimpleClientHandler;
import com.wuy.rpc.pojo.Constants;
import com.wuy.rpc.pojo.Response;

public class TcpClient {
	static final Bootstrap b=new Bootstrap();
    static ChannelFuture f=null;
    static{
         EventLoopGroup workerGroup = new NioEventLoopGroup();
         b.group(workerGroup); // (2)
         b.channel(NioSocketChannel.class); // (3)
         b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
         b.handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                 ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new SimpleClientHandler());
                ch.pipeline().addLast(new StringEncoder());
             }
         });
         CuratorFramework create = ZookeeperFactory.create();
         try {
			List<String> serverPaths = create.getChildren().forPath(Constants.SERVER_PATH);
			
			CuratorWatcher watcher=new ServerWatch();
			//加上 zk监听 服务器的变化
			create.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);
			
			for(String serverPath:serverPaths){
				String[] split = serverPath.split("#");
				int weight=Integer.valueOf(split[2]);
				if(weight>0){
					for(int w=0;w<weight;w++){
						ChannelManager.realServerPaths.add(split[0]+"#"+split[1]);
						try {
							ChannelFuture channelFuture = TcpClient.b.connect(split[0],Integer.valueOf(split[1]));
							ChannelManager.add(channelFuture);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
         
          
    }
    
    
    
    //发送数据
    public static Response send(ClientRequest request){
    	f=ChannelManager.get(ChannelManager.position);
        f.channel().writeAndFlush(JSONObject.toJSONString(request));
        f.channel().writeAndFlush("\r\n");
        DefaultFuture df = new DefaultFuture(request);
        Response response = df.get();
        DefaultFuture.allDefaultFuture.remove(request.getId());
        return response;
    }
	
}
