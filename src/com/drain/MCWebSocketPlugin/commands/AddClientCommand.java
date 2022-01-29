package com.drain.MCWebSocketPlugin.commands;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drain.bitcraft.BitcraftPlugin;
import com.drain.bitcraft.Configuration.AccessLevel;

import net.md_5.bungee.api.ChatColor;

public class AddClientCommand implements CommandExecutor {

	private BitcraftPlugin plugin;
	
	public AddClientCommand(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!sender.hasPermission("mcws.addclient")) {
			sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
			return true;
		}
		
		if(args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /mcws-addclient <name> <access level>");
			return true;
		}
		
		AccessLevel level;
		try {
			level = AccessLevel.fromInt(Integer.parseInt(args[1]));
		} catch(IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "Invalid access level!");
			return true;
		}
			
		if(plugin.config().getClient(args[0]) != null) {
			sender.sendMessage(ChatColor.RED + "A client with that ID exists already!");
			return true;
		}
		
		SecureRandom random = new SecureRandom();
		byte[] keyBuffer = new byte[64];
		random.nextBytes(keyBuffer);
		
		try {
			plugin.config().addCredentials(args[0], keyBuffer, level);
			sender.sendMessage(ChatColor.GREEN + "Generated new client. Its secret key is " + ChatColor.YELLOW + Base64.getEncoder().encodeToString(keyBuffer));
			sender.sendMessage(ChatColor.RED + "Store this key right now, as you will not be able to access it again. Make sure it hasn't been logged somewhere on your machine.");
		} catch(IOException exception) {
			sender.sendMessage(ChatColor.RED + "Failed to save configuration file while adding new client.");
		}
		
		return true;
		
	}
	
}
