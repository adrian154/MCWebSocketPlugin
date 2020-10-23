package com.drain.MCWebSocketPlugin.messages;

public class OutMessage {
	
	public String type;
	public long timestamp;
	
	public OutMessage(String type) {
		timestamp = System.currentTimeMillis();
		this.type = type;
	}
	
}