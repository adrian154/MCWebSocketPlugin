package com.drain.MCWebSocketPlugin.messages.outbound;

import com.drain.MCWebSocketPlugin.messages.inbound.Request;

public class ErrorResponse extends Response {

	private String message;
	
	public ErrorResponse(String message, Request request) {
		super(request);
		this.message = message;
	}
	
}
