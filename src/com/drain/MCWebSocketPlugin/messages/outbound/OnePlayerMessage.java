package com.drain.MCWebSocketPlugin.messages.outbound;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class OnePlayerMessage extends EventMessage {

	public UUID uuid;
	public String playerName;
	
	public OnePlayerMessage(String type, OfflinePlayer player) {
		super(type);
		this.uuid = player.getUniqueId();
		this.playerName = player.getName();
	}
	
}
