package com.wuy.rpc.client.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.wuy.rpc.pojo.Response;



public class DefaultFuture {
	public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<Long, DefaultFuture>();
	private CountDownLatch count = new CountDownLatch(1);
	private Response response;

	public DefaultFuture(ClientRequest request) {
		allDefaultFuture.put(request.getId(), this);
	}

	// 主线程获取数据，首先要等待结果
	public Response get() {
		try {
			boolean await = count.await(2*60, TimeUnit.SECONDS);
			//超时的 链路关闭
			if(!await){
				response=new Response();
				response.setId(-1L);
				response.setMsg("链路超时");
				response.setCode("404");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.response;
	}

	public static void recive(Response response) {
		DefaultFuture df = allDefaultFuture.get(response.getId());
		if (df != null) {
			try {
				df.setResponse(response);
				df.count.countDown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void setResponse(Response response) {
		this.response = response;
	}
}
