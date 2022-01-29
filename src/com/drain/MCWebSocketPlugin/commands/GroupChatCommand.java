package com.drain.MCWebSocketPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.drain.bitcraft.BitcraftPlugin;
import com.drain.bitcraft.Configuration;
import com.drain.bitcraft.Configuration.Group;

import net.md_5.bungee.api.ChatColor;

public class GroupChatCommand implements CommandExecutor {

	public BitcraftPlugin plugin;
	
	public GroupChatCommand(BitcraftPlugin plugin) { 
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "I'LL BE BACK...");
			return true;
		}
		
		Player player = (Player)sender;
		Configuration config = plugin.config();
		Group group = config.getGroup(player.getUniqueId());
		
		if(group == null) {
			sender.sendMessage(GroupCommand.ERROR_NOGROUP);
			return true;
		}
		
		String message = group.getColor().toString() + ChatColor.BOLD.toString() + "GROUP" + ChatColor.RESET + " " + player.getName() + ": " + String.join(" ", args);
		System.out.println(message);
		config.broadcastToGroup(group, message, plugin);
		
		return true;
		
	}

}
