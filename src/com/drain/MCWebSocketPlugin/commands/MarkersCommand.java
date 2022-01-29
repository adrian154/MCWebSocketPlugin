package com.drain.MCWebSocketPlugin.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;

import com.drain.bitcraft.BitcraftPlugin;

import net.md_5.bungee.api.ChatColor;

public class MarkersCommand implements CommandExecutor {

	private BitcraftPlugin plugin;
	
	public MarkersCommand(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Wait a sec... you're not a human!! AAAAH!!");
			return true;
		}
		
		Player player = (Player)sender;
		List<Integer> ids = plugin.config().getMarkers(player.getUniqueId());
		if(ids != null && ids.size() > 0) {
			player.sendMessage(ChatColor.DARK_GRAY + "========================================");
			for(int id: ids) {
				Marker marker = plugin.getMarker(player.getUniqueId(), id);
				if(marker != null) {
					player.sendMessage(ChatColor.GRAY + String.format("%d: \"%s\" (%d, %d)", id, marker.getLabel(), (int)marker.getX(), (int)marker.getZ()));
				}
			}
			player.sendMessage(ChatColor.DARK_GRAY + "========================================");
		} else {
			player.sendMessage(ChatColor.GRAY + "You have no markers. Try adding one with /setmarker");
		}
		
		return true;
		
	}
	
}
