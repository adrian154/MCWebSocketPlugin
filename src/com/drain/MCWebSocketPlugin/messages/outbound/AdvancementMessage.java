package com.drain.MCWebSocketPlugin.messages.outbound;

import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementMessage extends OnePlayerMessage {

	public String key;
	
	public AdvancementMessage(PlayerAdvancementDoneEvent event) {
		super("advancement", event.getPlayer());
		this.key = event.getAdvancement().getKey().toString();
	}
	
}
