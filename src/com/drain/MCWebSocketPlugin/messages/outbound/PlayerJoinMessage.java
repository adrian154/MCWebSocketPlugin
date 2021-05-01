package com.drain.MCWebSocketPlugin.messages.outbound;

import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinMessage extends OnePlayerMessage {

	public PlayerJoinMessage(PlayerJoinEvent event) {
		super("join", event.getPlayer());
	}
	
}