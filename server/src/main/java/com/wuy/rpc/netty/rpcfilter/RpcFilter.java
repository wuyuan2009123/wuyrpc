package com.wuy.rpc.netty.rpcfilter;

import java.lang.reflect.Method;

public interface RpcFilter {
	/**
	 * 拦截服务端开始前处理
	 * @param method
	 * @param bean
	 * @param requestObjects
	 * @return
	 */
	public boolean doBeforeRequest(Method method, Object bean, Object[] paramters);
	/**
	 * 拦截器服务端后处理
	 * @param result
	 * @return
	 */
	public void doAfterRequest(Object result);
}
