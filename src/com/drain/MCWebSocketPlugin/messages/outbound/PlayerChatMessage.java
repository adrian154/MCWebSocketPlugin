package com.drain.MCWebSocketPlugin.messages.outbound;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatMessage extends OnePlayerMessage {

	public String message;
	
	public PlayerChatMessage(AsyncPlayerChatEvent event) {
		super("chat", event.getPlayer());
		type = "chat";
		this.message = event.getMessage();
	}
	
}