package com.drain.MCWebSocketPlugin.messages.inbound;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;
import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;
import com.drain.MCWebSocketPlugin.messages.outbound.ErrorResponse;
import com.drain.MCWebSocketPlugin.messages.outbound.Response;

public class OnlinePlayersRequest extends Request {

	@Override
	public Response handle(MCWebSocketPlugin plugin, WebSocket socket) {
		
		if(plugin.getWSServer().getAccess(socket).contains(AccessLevel.GAME_INFO)) {
			return new PlayersResponse(plugin.getServer(), this);		
		}
		
		return new ErrorResponse("Not authorized", this);
		
	}
	
	private static class PlayersResponse extends Response {
		
		private Map<UUID, String> players;
		
		public PlayersResponse(Server server, Request request) {
			super(request);
			this.players = server.getOnlinePlayers().stream().collect(Collectors.toMap(Player::getUniqueId, Player::getName));
		}
			
	}
	
}
