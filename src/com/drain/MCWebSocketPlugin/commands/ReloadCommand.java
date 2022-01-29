package com.drain.MCWebSocketPlugin.commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drain.bitcraft.BitcraftPlugin;

import net.md_5.bungee.api.ChatColor;

public class ReloadCommand implements CommandExecutor {
	
	private BitcraftPlugin plugin;
	
	public ReloadCommand(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!sender.hasPermission("mcws.reload")) {
			sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
			return true;
		}
		
		try {
			plugin.config().reload();
			plugin.getWSServer().connectOutgoing();
			sender.sendMessage(ChatColor.GREEN + "Finished reloading!");
		} catch(IOException exception) {
			sender.sendMessage(ChatColor.RED + "Failed to restart MCWS. Check the server's logs for more info.");
			plugin.getLogger().severe(exception.getMessage());
		}
		
		return true;
		
	}
	
}
