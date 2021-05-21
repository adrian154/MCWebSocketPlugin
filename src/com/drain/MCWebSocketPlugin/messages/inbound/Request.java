package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;
import com.drain.MCWebSocketPlugin.messages.outbound.ErrorResponse;
import com.drain.MCWebSocketPlugin.messages.outbound.Response;
import com.google.gson.JsonSyntaxException;

public class Request {

	private String action;
	protected Integer id;
	
	protected static final String NOT_AUTHORIZED = "{\"error\": \"Not authorized\"}";
	protected static final String INVALID_FIELDS = "{\"error\": \"Invalid or missing fields\"}";
	protected static final String SUCCESS = "{\"error\": false}";
	protected static final String INVALID = "{\"error\": \"Invalid JSON\"}";
	
	private static final Map<String, Class<? extends Request>> inboundMessages = new HashMap<>();
	
	static {
		inboundMessages.put("auth", AuthRequest.class); 
		inboundMessages.put("runCommand", RunCommandRequest.class);
		inboundMessages.put("getOnline", OnlinePlayersRequest.class);
	}
	
	public final Response handle(MCWebSocketPlugin plugin, WebSocket socket, String json) {
		Class<? extends Request> clazz = inboundMessages.get(action);
		if(clazz != null) {
			try {
				Request message = plugin.getGson().fromJson(json, clazz);
				return message.handle(plugin, socket);
			} catch(JsonSyntaxException exception) {
				return new ErrorResponse("Invalid JSON", this);
			}
		}
		return new ErrorResponse("Unknown action", this);
	}

	public Response handle(MCWebSocketPlugin plugin, WebSocket socket) {
		throw new UnsupportedOperationException();
	}
	
	public Integer getID() {
		return this.id;
	}
	
}
