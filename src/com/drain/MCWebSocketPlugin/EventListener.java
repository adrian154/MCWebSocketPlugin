package com.drain.MCWebSocketPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.drain.MCWebSocketPlugin.messages.OutMessage;
import com.drain.MCWebSocketPlugin.messages.PlayerChatMessage;
import com.drain.MCWebSocketPlugin.messages.PlayerJoinMessage;
import com.drain.MCWebSocketPlugin.messages.PlayerQuitMessage;
import com.drain.MCWebSocketPlugin.messages.PlayerDeathMessage;
import com.google.gson.Gson;

public class EventListener implements Listener {

	private MCWebSocketPlugin plugin;
	private Gson gson;
	
	public EventListener(MCWebSocketPlugin plugin) {
		this.plugin = plugin;
		this.gson = new Gson();
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerJoinMessage(event));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerQuitMessage(event));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerChatMessage(event));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerDeathMessage(event));
	}
	
}
