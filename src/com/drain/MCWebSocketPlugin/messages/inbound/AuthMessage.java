package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.Base64;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;

public class AuthMessage extends InboundMessage {

	private String clientID, secret;
	
	@Override
	public void handle(MCWebSocketPlugin plugin, WebSocket socket) {
		if(secret != null && clientID != null) {
			try {
				byte[] key = Base64.getDecoder().decode(secret);
				if(plugin.getMCWSConfig().authClient(clientID, key)) {
					plugin.getWSServer().setAccess(socket, plugin.getMCWSConfig().getAccess(clientID));
					socket.send(SUCCESS);
				} else {
					socket.send(NOT_AUTHORIZED);
				}
			} catch(IllegalArgumentException exception) {
				socket.send(INVALID_FIELDS);
			}
		} else {
			socket.send(INVALID_FIELDS);
		}
	}
	
}
