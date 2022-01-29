package com.drain.MCWebSocketPlugin.commands;

import java.util.Arrays;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.drain.bitcraft.BitcraftPlugin;

import net.md_5.bungee.api.ChatColor;

public class MessageCommand implements CommandExecutor {

	private BitcraftPlugin plugin;
	
	public MessageCommand(BitcraftPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
			return true;
		}
		
		Player player = plugin.getServer().getPlayer(args[0]);
		if(player == null) {
			sender.sendMessage(ChatColor.RED + "Couldn't find " + args[0]);
			return true;
		}
		
		String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		player.sendMessage(ChatColor.YELLOW + "From " + sender.getName() + ": " + ChatColor.GRAY + message);
		sender.sendMessage(ChatColor.GREEN + "To " + player.getName() + ": " + ChatColor.GRAY + message);
		
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
		
		if(sender instanceof Player) {
			Player senderPlayer = (Player)sender;
			senderPlayer.playSound(senderPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
		}
		
		return true;
		
	}
	
}
