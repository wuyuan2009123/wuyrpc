package com.wuy.rpc.client.core;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
	static AtomicInteger position=new AtomicInteger(0);
	static CopyOnWriteArrayList<String> realServerPaths=new CopyOnWriteArrayList<String>();
	public static CopyOnWriteArrayList<ChannelFuture> channelFutures=new CopyOnWriteArrayList<ChannelFuture>();
	
	public static void removeChannel(ChannelFuture channel){
		channelFutures.remove(channel);
	}
	
	public static void add(ChannelFuture channel){
		channelFutures.add(channel);
	}
	
	public static void clear(){
		channelFutures.clear();
	}

	public static ChannelFuture get(AtomicInteger i) {
		int size=channelFutures.size();
		ChannelFuture channelFuture=null;
		if(i.get()>size){
			channelFuture=channelFutures.get(0);
			ChannelManager.position=new AtomicInteger(1);
		}else{
			channelFuture=channelFutures.get(i.getAndIncrement());
		}
		if(!channelFuture.channel().isActive()){
			channelFutures.remove(channelFuture);
			return get(position);
		}
		return channelFuture;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
