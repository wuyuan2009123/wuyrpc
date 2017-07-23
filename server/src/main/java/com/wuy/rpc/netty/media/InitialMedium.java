package com.wuy.rpc.netty.media;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.wuy.rpc.netty.annotation.Remote;
import com.wuy.rpc.netty.annotation.RpcFilterRemote;
import com.wuy.rpc.netty.rpcfilter.RpcFilter;

@Component
public class InitialMedium implements BeanPostProcessor {

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean.getClass().isAnnotationPresent(Remote.class)) {
			Method[] methods = bean.getClass().getDeclaredMethods();
			for (Method m : methods) {
				String key = bean.getClass().getInterfaces()[0].getName() + "."+ m.getName();
				Map<String, BeanMethod> beanMap = Media.beanMap;
				BeanMethod beanMethod = new BeanMethod();
				beanMethod.setBean(bean);
				beanMethod.setMethod(m);
				beanMap.put(key, beanMethod);
			}
		}
		
		if(bean.getClass().isAnnotationPresent(RpcFilterRemote.class)){
			RpcFilter rpcFilter=(RpcFilter)bean;
			RpcFilterRemote annotation = bean.getClass().getAnnotation(RpcFilterRemote.class);
			int value = annotation.value();
			Media.filterMap.put(value, rpcFilter);
		}

		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

}
