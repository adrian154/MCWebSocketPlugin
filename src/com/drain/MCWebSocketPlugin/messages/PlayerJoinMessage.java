package com.drain.MCWebSocketPlugin.messages;

import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinMessage extends OnePlayerMessage {

	public PlayerJoinMessage(PlayerJoinEvent event) {
		super("join", event.getPlayer());
	}
	
}