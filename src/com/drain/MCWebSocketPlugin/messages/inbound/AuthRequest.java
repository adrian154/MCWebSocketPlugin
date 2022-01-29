package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.Base64;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.messages.outbound.ErrorResponse;
import com.drain.MCWebSocketPlugin.messages.outbound.Response;
import com.drain.bitcraft.BitcraftPlugin;
import com.drain.bitcraft.Configuration.AccessLevel;
import com.drain.bitcraft.Configuration.Client;

public class AuthRequest extends Request {

	private String clientID, secret;
	
	@Override
	public Response handle(BitcraftPlugin plugin, WebSocket socket) {
		
		if(plugin.getWSServer().getClient(socket) != null) {
			return new ErrorResponse("Already authenticated", this);
		}
		
		if(secret == null || clientID == null) {
			return new ErrorResponse("Missing fields", this);
		}
		
		byte[] key;
		try {
			key = Base64.getDecoder().decode(secret);
		} catch(IllegalArgumentException exception) {
			return new ErrorResponse("Improperly formatted secret", this);
		}
		
		Client client = plugin.config().getClient(clientID);
		if(client == null) {
			return new ErrorResponse("No such client", this);
		}
		
		if(!client.auth(key)) {
			return new ErrorResponse("Authentication failed", this);
		}
		
		plugin.getWSServer().authClient(socket, client);
		return new AuthedResponse(client.getAccess(), this);
	
	}
	
	private static class AuthedResponse extends Response {
		
		private int accessLevel;
		
		public AuthedResponse(AccessLevel level, Request request) {
			super(request);
			this.accessLevel = level.toInt();
		}
		
	}
	
}
