package com.drain.MCWebSocketPlugin;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.drain.MCWebSocketPlugin.messages.InMessage;
import com.drain.MCWebSocketPlugin.messages.OutMessage;

public class WSServer extends WebSocketServer {

	private MCWebSocketPlugin plugin;
	private List<WebSocket> authedClients;

	public WSServer(InetSocketAddress addr, MCWebSocketPlugin plugin) {
		
		super(addr);
		this.plugin = plugin;
		this.authedClients = new ArrayList<WebSocket>();
		this.setReuseAddr(true);
		this.start();
		
	}
	
	public WSServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onClose(WebSocket conn, int arg1, String reason, boolean remote) {
		authedClients.remove(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println("an error occured on connection with " + conn.getRemoteSocketAddress() + ": " + ex.getMessage());
	}

	@Override
	public void onMessage(WebSocket conn, String strMessage) {
		InMessage message = plugin.getGson().fromJson(strMessage, InMessage.class);
		String response = message.execute(plugin, conn);
		if(response.length() > 0) {
			conn.send(response);
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("connection from " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onStart() {
		System.out.println("started listening for WebSocket connections");
	}

	public void authorize(WebSocket conn) {
		if(!authedClients.contains(conn)) {
			authedClients.add(conn);
		}
	}
	
	public boolean isAuthorized(WebSocket conn) {
		return authedClients.contains(conn);
	}
	
	public void broadcastMessage(OutMessage message) {
		broadcast(plugin.getGson().toJson(message));
	}
	
	public void broadcastMessageToAuthed(OutMessage message) {
		for(WebSocket conn: authedClients) {
			conn.send(plugin.getGson().toJson(message));
		}
	}
	
}
