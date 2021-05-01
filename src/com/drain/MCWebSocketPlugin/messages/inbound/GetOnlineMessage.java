package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;
import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;

public class GetOnlineMessage extends InboundMessage {

	@Override
	public void handle(MCWebSocketPlugin plugin, WebSocket socket) {
		
		if(plugin.getWSServer().getAccess(socket).allows(AccessLevel.GAME_INFO)) {
			
			List<PlayerPair> players = plugin.getServer().getOnlinePlayers()
				.stream()
				.map(player -> new PlayerPair(player))
				.collect(Collectors.toList());
			
			socket.send(plugin.getGson().toJson(players));
			
		} else {
			socket.send(NOT_AUTHORIZED);
		}
		
	}
	
	@SuppressWarnings("unused")
	private static class PlayerPair {
		
		public UUID uuid;
		public String name;
		
		public PlayerPair(Player player) {
			this.uuid = player.getUniqueId();
			this.name = player.getName();
		}
		
	}
	
}
