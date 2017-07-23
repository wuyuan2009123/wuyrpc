package com.wuy.rpc.client.core;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.wuy.rpc.client.annotation.RemoteInvoker;
import com.wuy.rpc.pojo.Response;

@Component
public class InvokerProxy implements BeanPostProcessor {
	

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		Field[] fields=bean.getClass().getDeclaredFields();
		for(Field field:fields){
			if(field.isAnnotationPresent(RemoteInvoker.class)){
				field.setAccessible(true);
				final Map<Method,Class<?>> methodClassMap=new HashMap<Method, Class<?>>();
				putMethodClass(methodClassMap,field);
				//动态代理
				Enhancer enhancer=new Enhancer();
				//设置字段的 类型
				enhancer.setInterfaces(new Class[]{field.getType()});
				enhancer.setCallback(new MethodInterceptor() {
					public Object intercept(Object bean, Method method, 
							Object[] args, MethodProxy poxy) throws Throwable {
						//采用 netty 调用服务器
						ClientRequest request = makeRequest(methodClassMap,
								method, args);
						Response response = TcpClient.send(request);
						return response;
					}

					private ClientRequest makeRequest(
							final Map<Method, Class<?>> methodClassMap,
							Method method, Object[] args) {
						ClientRequest request=new ClientRequest();
						request.setCommand(methodClassMap.get(method).getName()+"."+method.getName());
						request.setParameters(args);
						return request;
					}
				});
				
				try {
					field.set(bean, enhancer.create());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}
	
	private void putMethodClass(Map<Method, Class<?>> methodClassMap, Field field) {
		Method[] methods = field.getType().getDeclaredMethods();
		for(Method m:methods){
			methodClassMap.put(m, field.getType());
		}
	}
	
	
}
