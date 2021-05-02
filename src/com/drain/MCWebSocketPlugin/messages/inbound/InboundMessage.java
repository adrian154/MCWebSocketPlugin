package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;
import com.google.gson.JsonSyntaxException;

public class InboundMessage {

	private String action;
	
	protected static final String NOT_AUTHORIZED = "{\"error\": \"Not authorized\"}";
	protected static final String INVALID_FIELDS = "{\"error\": \"Invalid or missing fields\"}";
	protected static final String SUCCESS = "{\"error\": false}";
	protected static final String INVALID = "{\"error\": \"Invalid JSON\"}";
	
	private static final Map<String, Class<? extends InboundMessage>> inboundMessages = new HashMap<>();
	
	static {
		inboundMessages.put("auth", AuthMessage.class); 
		inboundMessages.put("runCommand", RunCommandMessage.class);
		inboundMessages.put("getOnline", GetOnlineMessage.class);
	}
	
	public InboundMessage() {
		
	}
	
	public final void execute(MCWebSocketPlugin plugin, WebSocket socket, String json) {
		Class<? extends InboundMessage> clazz = inboundMessages.get(action);
		if(clazz != null) {
			try {
				InboundMessage message = plugin.getGson().fromJson(json, clazz);
				message.handle(plugin, socket);
			} catch(JsonSyntaxException exception) {
				socket.send(INVALID);
			}
		}
	}

	public void handle(MCWebSocketPlugin plugin, WebSocket socket) {
		throw new UnsupportedOperationException();
	}
	
}
