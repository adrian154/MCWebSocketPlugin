package com.drain.MCWebSocketPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerChatMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerDeathMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerJoinMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerQuitMessage;

public class EventListener implements Listener {

	private MCWebSocketPlugin plugin;

	public EventListener(MCWebSocketPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerJoinMessage(event), AccessLevel.GAME_INFO);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerQuitMessage(event), AccessLevel.GAME_INFO);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerChatMessage(event), AccessLevel.GAME_INFO);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerDeathMessage(event), AccessLevel.GAME_INFO);
	}
	
}
