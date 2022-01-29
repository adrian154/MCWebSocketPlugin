package com.drain.MCWebSocketPlugin.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.drain.bitcraft.BitcraftPlugin;
import com.drain.bitcraft.Colors;
import com.drain.bitcraft.Configuration;
import com.drain.bitcraft.Configuration.Group;

public class GroupCommand implements CommandExecutor {

	public BitcraftPlugin plugin;
	
	private Map<Group, List<UUID>> invites;
	
	private static final String USAGE_CREATE = "/g create <name>";
	private static final String USAGE_MEMBERS = "/g members";
	private static final String USAGE_SETNAME = "/g setname <name>";
	private static final String USAGE_SETCOLOR = "/g setcolor <color>";
	private static final String USAGE_INVITE = "/g invite <player>";
	private static final String USAGE_UNINVITE = "/g uninvite <player>";
	private static final String USAGE_ACCEPT = "/g join <group>";
	private static final String USAGE_KICK = "/g kick <player>";
	private static final String USAGE_LEAVE = "/g leave";
	
	private static final String USAGE = ChatColor.RED + "Usage: ";
	private static final String USAGE_ALL = USAGE + USAGE_CREATE + "\n" +
										            USAGE_MEMBERS + "\n" +
										            USAGE_SETNAME + "\n" +
										            USAGE_SETCOLOR + "\n" + 
										            USAGE_INVITE + "\n" + 
										            USAGE_ACCEPT + "\n" +
										            USAGE_UNINVITE + "\n" + 
										            USAGE_KICK + "\n" +
										            USAGE_LEAVE;
	
	public static final String ERROR = ChatColor.RED + "Internal error occurred, contact an admin.";
	public static final String ERROR_NOGROUP = ChatColor.RED + "You're not in a group.";
	public static final String ERROR_NOPERMS = ChatColor.RED + "You're not the leader of your group.";
	public static final String ERROR_NOPLAYER = ChatColor.RED + "That player could not be found.";
	
	public GroupCommand(BitcraftPlugin plugin) { 
		this.plugin = plugin;
		this.invites = new HashMap<>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Non-human detected. Exterminating...");
			return true;
		}
		
		Player player = (Player)sender;
		UUID uuid = player.getUniqueId();
		Configuration config = plugin.config();
		Group group = config.getGroup(uuid);
		
		if(args.length < 1) {
			sender.sendMessage(USAGE_ALL);
			return true;
		}
		
		if(args[0].equals("create")) {
			
			if(group != null) {
				sender.sendMessage(ChatColor.RED + "You're already in a group, you need to leave yours first with /g leave before you can create a new one.");
				return true;
			}
			
			if(args.length != 2) {
				sender.sendMessage(USAGE + USAGE_CREATE);
				return true;
			}
			
			Group newGroup = new Group(args[1]);
			try {
				config.addGroup(newGroup);
				config.addPlayerToGroup(player.getUniqueId(), newGroup);
				newGroup.setAdmin(player.getUniqueId());
				config.save();
			} catch(IOException exception) {
				player.sendMessage(ERROR);
				return true;
			}
			
			player.sendMessage(ChatColor.GREEN + "Created your group!");
			plugin.config().updateName(player);
			
		} else if(args[0].equals("setname")) {
			
			if(group == null) {
				sender.sendMessage(ERROR_NOGROUP);
				return true;
			}
			
			if(!group.isAdmin(uuid)) {
				sender.sendMessage(ERROR_NOPERMS);
				return true;
			}
			
			if(args.length != 2) {
				sender.sendMessage(USAGE + USAGE_SETNAME);
				return true;
			}
			
			group.setName(args[1]);
			config.refreshGroupname(group, plugin);
			config.broadcastToGroup(group, ChatColor.GRAY + "The group was renamed to " + group.getDisplayName(), plugin);
			
		} else if(args[0].equals("setcolor")) {
			
			if(group == null) {
				sender.sendMessage(ERROR_NOGROUP);
				return true;
			}
			
			if(!group.isAdmin(uuid)) {
				sender.sendMessage(ERROR_NOPERMS);
				return true;
			}
			
			if(args.length != 2) {
				sender.sendMessage(USAGE + USAGE_SETCOLOR);
				return true;
			}
			
			ChatColor color = Colors.fromString(args[1]);
			if(color == null) {
				sender.sendMessage(ChatColor.RED + "Invalid color");
				return true;
			}
			
			group.setColor(color);
			config.refreshGroupname(group, plugin);
			
		} else if(args[0].equals("invite")) {
			
			if(group == null) {
				sender.sendMessage(ERROR_NOGROUP);
				return true;
			}
			
			if(!group.isAdmin(uuid)) {
				sender.sendMessage(ERROR_NOPERMS);
				return true;
			}
			
			if(args.length != 2) {
				sender.sendMessage(USAGE + USAGE_INVITE);
				return true;
			}
			
			Player toInvite = plugin.getServer().getPlayer(args[1]);
			if(toInvite == null) {
				sender.sendMessage(ERROR_NOPLAYER);
				return true;
			}
			
			if(group.getMembers().contains(toInvite.getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "That player is already in the group.");
				return true;
			}
			
			if(invites.get(group) == null) invites.put(group, new ArrayList<UUID>());
			invites.get(group).add(toInvite.getUniqueId());
			sender.sendMessage(ChatColor.GREEN + "Invited that player to the group!");
			
			toInvite.sendMessage(ChatColor.GREEN + "You've been invited to join " + group.getDisplayName());
			toInvite.sendMessage(ChatColor.GREEN + "Do /g join " + group.getName() + " to accept the invitation.");
			if(config.getGroup(toInvite.getUniqueId()) != null) {
				toInvite.sendMessage(ChatColor.YELLOW + "You will need to leave the group you are currently in with /g leave if you want to join a new group.");
			}
			
		} else if(args[0].equals("join")) {
			
			if(group != null) {
				sender.sendMessage(ChatColor.RED + "You're already in a group, you need to leave yours first with /g leave before you can create a new one.");
				return true;
			}
			
			if(args.length != 2) {
				sender.sendMessage(USAGE + USAGE_KICK);
				return true;	
			}
			
			Group toJoin = config.getGroup(args[1]);
			if(toJoin == null) {
				sender.sendMessage(ChatColor.RED + "No group with that name exists.");
				return true;
			}
			
			List<UUID> groupInvites = invites.get(toJoin);
			if(groupInvites == null || !groupInvites.contains(player.getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "You haven't been invited to join this group.");
				return true;
			}
			
			try {
				config.addPlayerToGroup(player.getUniqueId(), toJoin);
				config.updateName(player);
				config.broadcastToGroup(toJoin, ChatColor.GRAY + player.getName() + " joined the group", plugin);
			} catch(IOException e) {
				sender.sendMessage(ERROR);
				return true;
			}
			
		} else if(args[0].equals("uninvite")) {
			
			// TODO
			
		} else if(args[0].equals("kick")) {
			
			if(group == null) {
				sender.sendMessage(ERROR_NOGROUP);
				return true;
			}
			
			if(!group.isAdmin(uuid)) {
				sender.sendMessage(ERROR_NOPERMS);
				return true;
			}
			
			if(args.length != 2) {
				sender.sendMessage(USAGE + USAGE_KICK);
				return true;
			}
			
			OfflinePlayer toKick = plugin.getServer().getOfflinePlayer(args[1]);
			if(toKick == null) {
				sender.sendMessage(ERROR_NOPLAYER);
				return true;
			}
			
			if(!group.getMembers().contains(toKick.getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "That person isn't in your guild.");
				return true;
			}
			
			try {
				config.removePlayerFromGroup(toKick.getUniqueId(), group);
			} catch(IOException e) {
				sender.sendMessage(ERROR);
				return true;
			}
			
			config.broadcastToGroup(group, ChatColor.GRAY + toKick.getName() + " was kicked from the group", plugin);
			if(toKick.isOnline()) {
				Player kicked = plugin.getServer().getPlayer(toKick.getUniqueId());
				kicked.sendMessage(ChatColor.RED + "You were kicked from your group by its leader.");
				config.updateName(kicked);
			}
			
		} else if(args[0].equals("leave")) {
			
			if(group == null) {
				sender.sendMessage(ERROR_NOGROUP);
				return true;
			}
			
			try {
				config.removePlayerFromGroup(uuid, group);
			} catch(IOException e) {
				sender.sendMessage(ERROR);
				return true;
			}
			 
			sender.sendMessage(ChatColor.YELLOW + "You left the group.");
			config.updateName(player);
			config.broadcastToGroup(group, ChatColor.GRAY + player.getName() + " left the group", plugin);
			
		} else if(args[0].equals("members")) {
			
			if(group == null) {
				sender.sendMessage(ERROR_NOGROUP);
				return true;
			}
			
			ChatColor darker = Colors.getComplementary(group.getColor());
			player.sendMessage(darker + "========================================");
			for(UUID memberID: group.getMembers()) {
				OfflinePlayer member = plugin.getServer().getOfflinePlayer(memberID);
				player.sendMessage((memberID.equals(player.getUniqueId()) ? ChatColor.BOLD : "") + group.getColor().toString() + member.getName() + (group.isAdmin(memberID) ? " (LEADER)" : ""));
			}
			player.sendMessage(darker + "========================================");
			
		} else {
			player.sendMessage(ChatColor.RED + "Unknown subcommand. Do /g to see a list of things you can do.");
		}
		
		return true;
		
	}

}
