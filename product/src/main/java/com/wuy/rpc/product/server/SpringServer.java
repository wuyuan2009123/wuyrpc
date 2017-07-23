package com.wuy.rpc.product.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.wuy.rpc")
public class SpringServer {
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = new AnnotationConfigApplicationContext(
				SpringServer.class);
	}
}
