package com.drain.bitcraft;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.drain.MCWebSocketPlugin.messages.outbound.AdvancementMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerChatMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerDeathMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerJoinMessage;
import com.drain.MCWebSocketPlugin.messages.outbound.PlayerQuitMessage;
import com.drain.bitcraft.Configuration.AccessLevel;

public class EventListener implements Listener {

	private BitcraftPlugin plugin;

	public EventListener(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.getWSServer().broadcastMessage(new PlayerJoinMessage(event), AccessLevel.GAME_INFO);
		plugin.config().updateName(player);
		player.sendMessage(ChatColor.GOLD + "Please join the Discord server at https://discord.gg/2Usk25Kg47 if you haven't yet.");
		player.sendMessage(ChatColor.GREEN + "If you'd like to play with your friends, we would love for you to invite them too!");
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
		event.setJoinMessage(ChatColor.DARK_GREEN + "Join> " + ChatColor.GRAY + player.getName());
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerQuitEvent(org.bukkit.event.player.PlayerQuitEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerQuitMessage(event), AccessLevel.GAME_INFO);
		event.setQuitMessage(ChatColor.DARK_RED + "Quit> " + ChatColor.GRAY + event.getPlayer().getName());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerChatMessage(event), AccessLevel.GAME_INFO);
		event.setCancelled(true);
		plugin.getServer().broadcastMessage(String.format("%s%s: %s", event.getPlayer().getDisplayName(), ChatColor.RESET, event.getMessage()));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		plugin.getWSServer().broadcastMessage(new PlayerDeathMessage(event), AccessLevel.GAME_INFO);
		event.setDeathMessage(ChatColor.RED + event.getDeathMessage());
	}
	
	// some fluff is necessary to filter out unimportant events
	// since this event is called when a player completes any advancement criteria...
	@EventHandler(priority=EventPriority.MONITOR)
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		AdvancementProgress progress = event.getPlayer().getAdvancementProgress(event.getAdvancement());
		if(progress.isDone()) {
			plugin.getWSServer().broadcastMessage(new AdvancementMessage(event), AccessLevel.GAME_INFO);
		}
	}
	
}
