package com.drain.MCWebSocketPlugin.commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;

import net.md_5.bungee.api.ChatColor;

public class ReloadCommand implements CommandExecutor {
	
	private MCWebSocketPlugin plugin;
	
	public ReloadCommand(MCWebSocketPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		try {
			plugin.getMCWSConfig().reload();
			sender.sendMessage(ChatColor.GREEN + "Reloaded configuration successfully!");
		} catch(IOException exception) {
			sender.sendMessage(ChatColor.RED + "Failed to reload configuration. Check the server's logs for more info.");
			plugin.getLogger().severe(exception.getMessage());
		}
		
		return true;
		
	}
	
}
