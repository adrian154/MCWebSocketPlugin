package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.Base64;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.Configuration.Client;
import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;

public class AuthMessage extends InboundMessage {

	// errors
	private static final String ALREADY_AUTHED = "{\"error\": \"Already authenticated\"}";
	private static final String NO_SUCH_CLIENT = "{\"error\": \"No such client\"}";
	
	private String clientID, secret;
	
	@Override
	public void handle(MCWebSocketPlugin plugin, WebSocket socket) {
		
		if(plugin.getWSServer().getClient(socket) != null) {
			socket.send(ALREADY_AUTHED);
			return;
		}
		
		if(secret != null && clientID != null) {
			
			byte[] key = Base64.getDecoder().decode(secret);
			Client client = plugin.getMCWSConfig().getClient(clientID);
			if(client == null) {
				socket.send(NO_SUCH_CLIENT);
				return;
			}
			
			if(client.auth(key)) {
				plugin.getWSServer().authClient(socket, client);
				socket.send(SUCCESS);
			} else {
				socket.send(NOT_AUTHORIZED);
			}
			
		} else {
			socket.send(INVALID_FIELDS);
		}
		
	}
	
}
