package com.drain.MCWebSocketPlugin.messages;

import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitMessage extends OnePlayerMessage {

	public PlayerQuitMessage(PlayerQuitEvent event) {
		super("quit", event.getPlayer());
	}
	
}
