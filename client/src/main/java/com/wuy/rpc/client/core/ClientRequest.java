package com.wuy.rpc.client.core;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {
	private final long id;
	private Object[] parameters;
	private static final AtomicLong aid = new AtomicLong(1);

	private String command;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ClientRequest() {
		id = aid.incrementAndGet();
	}

	public long getId() {
		return id;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	
}
