package com.drain.MCWebSocketPlugin.messages;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class OnePlayerMessage extends OutMessage {

	public UUID uuid;
	public String playerName;
	
	public OnePlayerMessage(String type, OfflinePlayer player) {
		super(type);
		this.uuid = player.getUniqueId();
		this.playerName = player.getName();
	}
	
}
