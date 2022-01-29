package com.drain.bitcraft;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Configuration {

	// --- static fields
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	// To add new access levels, just add a new enum and bump the permission levels on the default ones
	// TODO: Better access control.
	public static enum AccessLevel {
		
		NONE(0),
		GAME_INFO(1),
		CONSOLE_READONLY(2),
		CONSOLE(3);
		
		private int level;
		private AccessLevel(int level) {
			this.level = level;
		}
		
		public boolean contains(AccessLevel level) {
			return this.level >= level.level;
		}
		
		public static AccessLevel fromInt(int level) {
			if(level < 0 || level >= AccessLevel.values().length) throw new IllegalArgumentException();
			return AccessLevel.values()[level];
		}
		
		public int toInt() {
			return this.level;
		}
		
	};
	
	// --- fields
	private ConfigContainer config;
	private File configFile;
	private static final MessageDigest keyHasher;
	
	static {
		try {
			keyHasher = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException exception) {
			throw new RuntimeException("Couldn't create key hash.");
		}
	}
	
	// --- constructors
	public Configuration() throws IOException {
		this.configFile = new File("plugins/MCWebSocket/config.json");
		this.reload();
	}
	
	// --- private methods
	private static byte[] hashKey(byte[] key) {
		return keyHasher.digest(key);
	}
	
	// --- public methods
	public void save() throws IOException {
		PrintWriter pw = new PrintWriter(configFile.getPath());
		pw.println(gson.toJson(config));
		pw.close();
	}

	public void reload() throws IOException {
		if(configFile.exists()) {
			String json = new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8);
			config = gson.fromJson(json, ConfigContainer.class);
			config.afterLoad();
		} else {
			config = new ConfigContainer();
			File directory = new File("plugins/MCWebSocket");
			if(!directory.exists()) {
				directory.mkdir();
			}
			configFile.createNewFile();
			save();
		}
	}
	
	public void addCredentials(String clientID, byte[] key, AccessLevel level) throws IOException {
		config.clients.put(clientID, new Client(key, level, clientID));
		save();
	}

	public void addGroup(Group group) throws IOException {
		config.groups.add(group);
		save();
	}

	public void addPlayerToGroup(UUID uuid, Group group) throws IOException {
		group.addMember(uuid);
		config.groupMap.put(uuid, group);
		save();
	}
	
	public void removePlayerFromGroup(UUID uuid, Group group) throws IOException {
		group.members.remove(uuid);
		config.groupMap.put(uuid, null);
		save();
	}
	
	public Group getGroup(UUID uuid) {
		return config.groupMap.get(uuid);
	}
	
	public Group getGroup(String groupName) {
		for(Group group: config.groups) {
			if(group.name.equalsIgnoreCase(groupName)) {
				return group;
			}
		}
		return null;
	}
	
	public void updateName(Player player) {
		Group group = this.getGroup(player.getUniqueId());
		if(group != null) {
			String name = group.getColor() + "[" + group.getName() + "] " + player.getName();
			player.setDisplayName(name);
			player.setPlayerListName(" " + name);
		} else {
			player.setDisplayName(player.getName());
			player.setPlayerListName(null);
		}
	}
	
	public void refreshGroupname(Group group, BitcraftPlugin plugin) {
		for(UUID uuid: group.members) {
			Player player = plugin.getServer().getPlayer(uuid);
			if(player != null) {
				updateName(player);
			}
		}
	}
	
	public void broadcastToGroup(Group group, String string, BitcraftPlugin plugin) {
		for(UUID uuid: group.members) {
			Player player = plugin.getServer().getPlayer(uuid);
			if(player != null) {
				player.sendMessage(string);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.5F);
			}
		}
	}
	
	public List<String> getOutgoingHosts() { return config.outgoingHosts; }
	public int getPort() { return config.port; }
	public AccessLevel getDefaultAccess() { return config.defaultAccessLevel; }
	public Client getClient(String name) { return config.clients.get(name); }
	public String getServerIDSecret() { return config.serverIDSecret; }
	
	public List<Integer> getMarkers(UUID uuid) {
		if(config.markers.get(uuid) == null) {
			config.markers.put(uuid, new ArrayList<>());
		}
		return config.markers.get(uuid);
	}
	
	// --- inner classes
	private class ConfigContainer {
		
		public Map<String, Client> clients;
		private List<Group> groups;
		public Map<UUID, List<Integer>> markers;
		public List<String> outgoingHosts;
		public int defaultAccess;
		public int port;
		public String serverIDSecret;
		
		public transient Map<UUID, Group> groupMap;
		public transient AccessLevel defaultAccessLevel;
		
		public ConfigContainer() {
			
			// defaults
			clients = new HashMap<>();
			markers = new HashMap<>();
			groups = new ArrayList<>();
			outgoingHosts = new ArrayList<>();
			port = 1738;
			defaultAccess = AccessLevel.NONE.ordinal();
			defaultAccessLevel = AccessLevel.NONE;
			
			byte[] serverIDSecretBuf = new byte[64];
			SecureRandom random = new SecureRandom();
			random.nextBytes(serverIDSecretBuf);
			serverIDSecret = Base64.getEncoder().encodeToString(serverIDSecretBuf);
		
		}
		
		public void afterLoad() {
			
			defaultAccessLevel = AccessLevel.fromInt(defaultAccess);
			for(Client client: clients.values()) {
				client.complete();
			}
			
			this.groupMap = new HashMap<>();
			for(Group group: groups) {
				for(UUID uuid: group.members) {
					groupMap.put(uuid,  group);
				}
			}
		
		}
		
	}
	
	public static class Client {
		 
		// json fields
		private int accessLevel;
		private String keyHash;
		private String clientID;
		
		// real fields
		private transient byte[] secretHash;
		private transient AccessLevel access;
		
		public Client(byte[] secret, AccessLevel access, String clientID) {
			this.secretHash = hashKey(secret);
			this.access = access;
			this.accessLevel = access.ordinal();
			this.keyHash = Base64.getEncoder().encodeToString(secretHash);
			this.clientID = clientID;
		}
		
		// finish deserialization process
		// GSON *does* have provisions for functionality like this BUT...
		// for the sake of simplicity, I opted for the caveman approach
		public void complete() {
			if(keyHash == null) {
				throw new IllegalArgumentException("Client has no key!");
			}
			this.secretHash = Base64.getDecoder().decode(keyHash);
			this.access = AccessLevel.fromInt(accessLevel);
		}
		
		public boolean auth(byte[] key) {
			
			byte[] hashed = hashKey(key);
			
			// a pitiful attempt at an equal time comparison
			// why even bother :-|
			boolean equal = true;
			for(int i = 0; i < hashed.length; i++) {
				if(hashed[i] != secretHash[i]) equal = false;
			}
			
			return equal;

		}
		
		public String getID() { return clientID; }
		public AccessLevel getAccess() { return access; }
		
	}
	
	public static class Group {
		
		private List<UUID> members;
		private String name;
		private ChatColor color;
		private UUID admin;
		
		public Group(String name) {
			this.name = name;
			this.members = new ArrayList<>();
			this.color = ChatColor.YELLOW;
		}
		
		public ChatColor getColor() {
			return color;
		}
		
		public void setColor(ChatColor color) {
			this.color = color;
		}
		
		public String getDisplayName() {
			return color + name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public List<UUID> getMembers() {
			return members;
		}
		
		public boolean isAdmin(UUID uuid) {
			return admin.equals(uuid);
		}
		
		public void setAdmin(UUID uuid) {
			this.admin = uuid;
		}
		
		protected void addMember(UUID uuid) {
			this.members.add(uuid);
		}
		
		protected void removeMember(UUID uuid) {
			this.members.remove(uuid);
		}
		
	}

}