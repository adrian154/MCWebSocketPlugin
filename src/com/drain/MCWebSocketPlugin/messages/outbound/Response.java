package com.drain.MCWebSocketPlugin.messages.outbound;

import com.drain.MCWebSocketPlugin.messages.inbound.Request;
import com.google.gson.Gson;

public class Response {

	private static final Gson gson = new Gson();
	
	private Integer requestID;
	
	public Response(int requestID) {
		this.requestID = requestID;
	}
	
	public Response(Request request) {
		this.requestID = request.getID();
	}
	
	@Override
	public String toString() {
		return gson.toJson(this);
	}
	
}
