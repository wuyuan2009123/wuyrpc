package com.wuy.rpc.client.core;


import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import com.wuy.rpc.client.factory.ZookeeperFactory;
import com.wuy.rpc.pojo.Constants;

public class ServerWatch implements CuratorWatcher {

	public void process(WatchedEvent event) throws Exception {
		CuratorFramework create = ZookeeperFactory.create();
		String path=event.getPath();
		create.getChildren().usingWatcher(this).forPath(Constants.SERVER_PATH);
		List<String> serverPaths=create.getChildren().forPath(path);
		ChannelManager.realServerPaths=new CopyOnWriteArrayList<String>();
		//清空
		for(String serverPath:serverPaths){
			String[] split = serverPath.split("#");
			int weight=Integer.valueOf(split[2]);
			for(int w=0;w<weight;w++){
				ChannelManager.realServerPaths.add(split[0]+"#"+split[1]+"#"+split[2]);
			}
		}
		//清空 channelFuture
		ChannelManager.clear();
		if(ChannelManager.realServerPaths.size()>0){
			for(String serverPath:ChannelManager.realServerPaths){
				String[] split = serverPath.split("#");
				int weight=Integer.valueOf(split[2]);
				for(int w=0;w<weight;w++){
					try {
						ChannelFuture channelFuture = TcpClient.b.connect(split[0],Integer.valueOf(split[1]));
						ChannelManager.add(channelFuture);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
