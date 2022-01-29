package com.drain.MCWebSocketPlugin.commands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.drain.bitcraft.BitcraftPlugin;

import net.md_5.bungee.api.ChatColor;

public class SetMarkerCommand implements CommandExecutor {

	private BitcraftPlugin plugin;
	
	public SetMarkerCommand(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Bad bot!");
			return true;
		}
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /setmarker <icon> <name>");
			return true;
		}
		
		try {
			if(plugin.addMarker((Player)sender, args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)))) {
				sender.sendMessage(ChatColor.GREEN + "Added marker!");
				plugin.config().save();
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid icon name.");
			}
		} catch(IOException e) {
			sender.sendMessage(ChatColor.RED + "Failed to save the server configuration. Tell an admin.");
		}
		
		return true;
		
	}
	
}
