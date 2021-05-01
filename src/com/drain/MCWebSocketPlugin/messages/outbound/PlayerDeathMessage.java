package com.drain.MCWebSocketPlugin.messages.outbound;

import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathMessage extends OnePlayerMessage {
	
	public String deathMessage;
	
	public PlayerDeathMessage(PlayerDeathEvent event) {
		super("death", event.getEntity());
		this.deathMessage = event.getDeathMessage();
	}
	
}