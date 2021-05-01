package com.drain.MCWebSocketPlugin.messages.outbound;

public class OutboundMessage {
	
	public String type;
	public long timestamp;
	
	public OutboundMessage(String type) {
		timestamp = System.currentTimeMillis();
		this.type = type;
	}
	
}