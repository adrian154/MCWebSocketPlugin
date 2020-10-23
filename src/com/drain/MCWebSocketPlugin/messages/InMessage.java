package com.drain.MCWebSocketPlugin.messages;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;

public class InMessage {

	public String type;
	
	// Used for message event
	public String discordTag; // sender tag
	public String message; // message
	public String secret; // client secret
	public String command; // which command
	
	// No args constructor...
	public InMessage() {
		
	}
	
	public String execAuth(MCWebSocketPlugin plugin, WebSocket conn) {
		if(secret != null && secret.equals("exi+EkWqZX+/JnCJNGMD1lIMkOneAEVpC4Pw719xDB951U0A8iInlPCiYKk2m3dZjPttI8dWKkVYtSuuoGf6WQ==")) {
			plugin.getWSServer().authorize(conn);
			return "";
		} else {
			return "{\"error\":\"Incorrect or missing client secret\"}";
		}
	}
	
	public String execMessage(MCWebSocketPlugin plugin, WebSocket conn) {
		if(plugin.getWSServer().isAuthorized(conn)) {
			plugin.getServer().broadcastMessage(String.format("[Discord] %s: %s", discordTag, message));
			return "";
		} else {
			return "{\"error\":\"Not authorized\"}";
		}
	}
	
	public String execGetOnline(MCWebSocketPlugin plugin, WebSocket conn) {
		return plugin.getGson().toJson(new OnlinePlayers(plugin));
	}
	
	public String execRunCommand(MCWebSocketPlugin plugin, WebSocket conn) {
		if(plugin.getWSServer().isAuthorized(conn)) {
			plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
				public void run() {
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
				}
			});
			return "";
		} else {
			return "{\"error\":\"Not authorized\"}";
		}
	}
	
	// Return JSON string
	public String execute(MCWebSocketPlugin plugin, WebSocket conn) {
		
		switch(type) {
			case "auth": return execAuth(plugin, conn);
			case "message": return execMessage(plugin, conn);
			case "getOnline": return execGetOnline(plugin, conn);
			case "runCommand": return execRunCommand(plugin, conn);
			default: return "";
		}
		
	}
	
	private static class OnlinePlayers {

		public List<PlayerPair> data;
		
		public OnlinePlayers(MCWebSocketPlugin plugin) {
			
			data = plugin.getServer().getOnlinePlayers()
					.stream()
					.map(player -> new PlayerPair(player))
					.collect(Collectors.toList());
			
		}
		
		private static class PlayerPair {
			
			public UUID uuid;
			public String name;
			
			public PlayerPair(Player player) {
				this.uuid = player.getUniqueId();
				this.name = player.getName();
			}
			
		}
		
	}
	
}
