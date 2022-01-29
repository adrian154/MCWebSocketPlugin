package com.drain.MCWebSocketPlugin.commands;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.java_websocket.WebSocket;

import com.drain.bitcraft.BitcraftPlugin;
import com.drain.bitcraft.Configuration.Client;

import net.md_5.bungee.api.ChatColor;

public class StatusCommand implements CommandExecutor {
	
	private BitcraftPlugin plugin;
	
	public StatusCommand(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!sender.hasPermission("mcws.status")) {
			sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
			return true;
		}
		
		Map<WebSocket, Client> statuses = plugin.getWSServer().getClients();
		for(WebSocket socket: statuses.keySet()) {
			Client client = statuses.get(socket);
			sender.sendMessage(String.format("%s: %s, %s", client.getID(), socket.isOpen() ? "OPEN" : "CLOSED", socket.getRemoteSocketAddress().toString()));
		}
		
		return true;
		
	}
	
}
