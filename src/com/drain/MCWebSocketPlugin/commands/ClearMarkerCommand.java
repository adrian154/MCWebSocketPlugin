package com.drain.MCWebSocketPlugin.commands;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;

import com.drain.bitcraft.BitcraftPlugin;

import net.md_5.bungee.api.ChatColor;

public class ClearMarkerCommand implements CommandExecutor {

	public BitcraftPlugin plugin;
	
	public ClearMarkerCommand(BitcraftPlugin plugin) { 
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Let me put this in a language you can understand. BEEP BOOP BOOP BEEP BEEP");
			return true;
		}

		if(args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /clearmarker <number>");
			sender.sendMessage(ChatColor.RED + "Do /markers to see a list of markers you've placed.");
		}
		
		
		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "You didn't enter a valid number.");
			return true;
		}
		
		Player player = (Player)sender;
		List<Integer> ids = plugin.config().getMarkers(player.getUniqueId());
		if(ids != null && ids.size() > 0) {
			Marker marker = plugin.getMarker(player.getUniqueId(), id);
			if(marker != null) {
				try {
					marker.deleteMarker();
					ids.remove((Integer)id);
					player.sendMessage(ChatColor.GREEN + String.format("Removed \"%s\"!", marker.getLabel()));
					plugin.config().save();
				} catch(IOException e) {
					player.sendMessage(ChatColor.RED + "Failed to save the server configuration. Tell an admin.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "There's no such marker.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You haven't placed any markers.");
		}
		
		return true;
		
	}
	
}
