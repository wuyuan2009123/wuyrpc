package com.wuy.rpc.product.filter;

import java.lang.reflect.Method;

import com.wuy.rpc.netty.annotation.RpcFilterRemote;
import com.wuy.rpc.netty.rpcfilter.RpcFilter;

@RpcFilterRemote(0)
public class RpcFilterDemo implements RpcFilter {

	public boolean doBeforeRequest(Method method, Object bean,
			Object[] paramters) {
		System.out.println("doBeforeRequest , methodName:"+method.getName()+"  ,Bean :"+bean.getClass()+", paramters:"+paramters);
		return true;
	}

	public void doAfterRequest(Object result) {
		System.out.println("doAfterRequest result : "+result);
	}

}
