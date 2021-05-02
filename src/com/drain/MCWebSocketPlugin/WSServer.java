package com.drain.MCWebSocketPlugin;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;
import com.drain.MCWebSocketPlugin.Configuration.Client;
import com.drain.MCWebSocketPlugin.messages.inbound.InboundMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.OutboundMessage;
import com.google.gson.JsonSyntaxException;

public class WSServer extends WebSocketServer {

	// --- fields
	private MCWebSocketPlugin plugin;
	private Map<WebSocket, Client> clients;
	private Map<String, WebSocket> outgoing;
	
	// --- constructors
	public WSServer(InetSocketAddress addr, MCWebSocketPlugin plugin) {
		super(addr);
		this.plugin = plugin;
		this.clients = new HashMap<WebSocket, Client>();
		this.outgoing = new HashMap<String, WebSocket>();
		this.setReuseAddr(true);
		this.start();
		this.connectOutgoing();
	}

	// --- public methods
	public Client getClient(WebSocket socket) {
		return clients.get(socket);
	}
	
	public Map<WebSocket, Client> getClients() {
		return clients;
	}
	
	public void connectOutgoing() {
		for(String host: plugin.getMCWSConfig().getOutgoingHosts()) {
			WebSocket socket = outgoing.get(host);
			if(socket == null || !socket.isOpen()) {
				try {
					outgoing.put(host, new OutgoingClient(host, this));
					plugin.getLogger().info("Connected to " + host);
				} catch(URISyntaxException exception) {
					plugin.getLogger().warning(String.format("Invalid host url \"%s\": %s", host, exception.getMessage()));
				}
			}
		}
	}

	public AccessLevel getAccess(WebSocket socket) {
		Client client = clients.get(socket);
		if(client == null) return AccessLevel.NONE;
		return client.getAccess();
	}
	
	public void authClient(WebSocket socket, Client client) {
		clients.put(socket, client);
	}
	
	public void broadcastMessage(OutboundMessage message, AccessLevel minimum) {
		String json = plugin.getGson().toJson(message);
		for(WebSocket conn: clients.keySet()) {
			if(clients.get(conn).getAccess().contains(minimum)) {
				conn.send(json);
			}
		}
	}

	// --- implemented methods
	@Override
	public void onClose(WebSocket socket, int code, String reason, boolean remote) {
		clients.remove(socket);
	}

	@Override
	public void onError(WebSocket socket, Exception ex) {
		// TODO: logging
	}

	@Override
	public void onMessage(WebSocket socket, String strMessage) {
		try {
			InboundMessage message = plugin.getGson().fromJson(strMessage, InboundMessage.class);
			message.execute(plugin, socket, strMessage);
		} catch(JsonSyntaxException excption) {
			plugin.getLogger().warning("Ignoring malformed message");
		}
	}

	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) { }

	@Override
	public void onStart() {
		plugin.getLogger().info("Started listening for websocket connections");
	}
	
	// --- inner classes
	private class OutgoingClient extends WebSocketClient {
		
		private WSServer server;
		
		public OutgoingClient(String host, WSServer server) throws URISyntaxException {
			super(new URI(host));
			this.server = server;
			this.connect();
		}

		@Override
		public void onOpen(ServerHandshake handshake) { }
		
		@Override
		public void onMessage(String message) {
			server.onMessage(this, message);
		}
		
		@Override
		public void onClose(int code, String reason, boolean remote) {
			clients.remove(this);
		}
		
		@Override
		public void onError(Exception exception) {
			// TODO: logging
		}
		
	}
	
}
