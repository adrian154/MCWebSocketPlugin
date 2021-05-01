package com.drain.MCWebSocketPlugin.messages.inbound;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;

public class InboundMessage {

	private String action;
	
	protected static final String NOT_AUTHORIZED = "{\"error\": \"Not authorized\"}";
	protected static final String INVALID_FIELDS = "{\"error\": \"Invalid or missing fields\"}";
	protected static final String SUCCESS = "{\"error\": false}";
	
	private static final Map<String, Class<? extends InboundMessage>> inboundMessages = new HashMap<>();
	
	static {
		inboundMessages.put("auth", AuthMessage.class); 
		inboundMessages.put("runCommand", RunCommandMessage.class);
	}
	
	public InboundMessage() {
		
	}
	
	public final void execute(MCWebSocketPlugin plugin, WebSocket socket) {
		Class<? extends InboundMessage> clazz = inboundMessages.get(action);
		if(clazz != null) {
			try {
				InboundMessage message = clazz.getConstructor(InboundMessage.class).newInstance(this);
				message.handle(plugin, socket);
			} catch(NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				throw new RuntimeException("Failed to instantiate inbound message");
			}
		}
	}

	public void handle(MCWebSocketPlugin plugin, WebSocket socket) {
		socket.send("{\"error\": \"Unknown command\"}");
	}
	
}
