package com.drain.MCWebSocketPlugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;
import com.drain.MCWebSocketPlugin.messages.inbound.InboundMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.OutboundMessage;

public class WSServer extends WebSocketServer {

	private MCWebSocketPlugin plugin;
	private Map<WebSocket, AccessLevel> clients;
	
	public WSServer(InetSocketAddress addr, MCWebSocketPlugin plugin) {
		super(addr);
		this.plugin = plugin;
		this.clients = new HashMap<WebSocket, AccessLevel>();
		this.setReuseAddr(true);
		this.start();
	}
	
	public WSServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onClose(WebSocket conn, int arg1, String reason, boolean remote) {
		clients.remove(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		plugin.getLogger().warning("An error occured on connection with " + conn.getRemoteSocketAddress() + ": " + ex.getMessage());
	}

	@Override
	public void onMessage(WebSocket socket, String strMessage) {
		InboundMessage message = plugin.getGson().fromJson(strMessage, InboundMessage.class);
		message.execute(plugin, socket, strMessage);
	}

	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) {
		clients.put(socket, plugin.getMCWSConfig().getDefaultAccess());
	}

	@Override
	public void onStart() {
		System.out.println("started listening for WebSocket connections");
	}

	public AccessLevel getAccess(WebSocket socket) {
		return clients.containsKey(socket) ? clients.get(socket) : AccessLevel.NONE;
	}
	
	public void setAccess(WebSocket conn, AccessLevel level) {
		clients.put(conn, level);
	}
	
	public void broadcastMessage(OutboundMessage message, AccessLevel minimum) {
		String json = plugin.getGson().toJson(message);
		for(WebSocket conn: clients.keySet()) {
			if(clients.get(conn).allows(minimum)) {
				conn.send(json);
			}
		}
	}
	
}
