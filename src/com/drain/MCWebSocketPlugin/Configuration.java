package com.drain.MCWebSocketPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class Configuration {

	public static enum AccessLevel {
		
		NONE(0),
		GAME_INFO(1),
		ALL_EVENTS(2),
		FULL_ACCESS(3);
		
		private int level;
		private AccessLevel(int level) {
			this.level = level;
		}
		
		public boolean allows(AccessLevel level) {
			return this.level >= level.level;
		}
		
		public static AccessLevel fromInt(int level) {
			if(level < 0 || level >= AccessLevel.values().length) throw new IllegalArgumentException();
			return AccessLevel.values()[level];
		}
		
	};
	
	private ConfigContainer config;
	private Gson gson;
	private File configFile;
	private MessageDigest keyHasher;
	private MCWebSocketPlugin plugin;
	
	public Configuration(MCWebSocketPlugin plugin) throws IOException {
		this.plugin = plugin;
		this.gson = new Gson();
		this.configFile = new File("plugins/MCWebSocket/config.json");
		try {
			this.keyHasher = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException exception) {
			throw new RuntimeException("Could not get create message digest for hashing API secrets.");
		}
		this.reload();
	}
	
	public void save() throws IOException {
		PrintWriter pw = new PrintWriter(configFile.getPath());
		pw.println(gson.toJson(config));
		pw.close();
	}
	
	private byte[] hashKey(byte[] key) {
		return keyHasher.digest(key);
	}
	
	// --- methods
	public void reload() throws IOException {
		if(configFile.exists()) {
			String json = new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8);
			config = gson.fromJson(json, ConfigContainer.class);
			config.afterLoad();
		} else {
			config = new ConfigContainer();
			configFile.createNewFile();
			save();
		}
	}
	
	public void addCredentials(String clientID, byte[] key, AccessLevel level) throws IOException {
		config.keys.put(clientID, hashKey(key));
		config.accessLevels.put(clientID, level);
		save();
	}
	
	public boolean authClient(String clientID, byte[] key) {
		return config.keys.get(clientID).equals(hashKey(key));
	}
	
	public List<String> getOutgoingHosts() { return config.outgoingHosts; }
	public int getPort() { return config.port; }
	public AccessLevel getAccess(String clientID) { return config.accessLevels.get(clientID); }
	public AccessLevel getDefaultAccess() { return config.defaultAccess; }
	
	// --- classes
	private class ConfigContainer {
		
		public Map<String, byte[]> keys;
		public Map<String, AccessLevel> accessLevels;
		public List<String> outgoingHosts;
		public AccessLevel defaultAccess;
		public int defaultAccessInt;
		public int port;
		
		public ConfigContainer() {
			keys = new HashMap<String, byte[]>();
			accessLevels = new HashMap<String, AccessLevel>();
			outgoingHosts = new ArrayList<String>();
			port = 17224; // :)
			defaultAccess = AccessLevel.NONE;
		}
		
		public void afterLoad() {
			
			defaultAccess = AccessLevel.fromInt(defaultAccessInt);
			
			for(String clientID: accessLevels.keySet()) {
				if(!keys.containsKey(clientID)) {
					plugin.getLogger().warning(String.format("Removing access for client %s since it has no key", clientID));
					accessLevels.remove(clientID);
				}
			}
			
			for(String clientID: keys.keySet()) {
				if(!accessLevels.containsKey(clientID)) {
					plugin.getLogger().warning(String.format("Resetting access for client %s to default level %s since it is missing from the accessLevels map", clientID, defaultAccess.toString()));
					accessLevels.put(clientID, defaultAccess);
				}
			}
			
		}
		
	}

}