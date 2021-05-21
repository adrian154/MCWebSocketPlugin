package com.drain.MCWebSocketPlugin.messages.outbound;

public class EventMessage {
	
	public String type;
	public long timestamp;
	
	public EventMessage(String type) {
		timestamp = System.currentTimeMillis();
		this.type = type;
	}
	
}