package com.drain.MCWebSocketPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Configuration {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
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
	private File configFile;
	private MessageDigest keyHasher;

	public Configuration() throws IOException {
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
	
	private Client getClient(String name) {
		if(!config.clients.containsKey(name)) {
			throw new IllegalArgumentException("No such client.");
		}
		return config.clients.get(name);
	}
	
	// --- methods
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
		config.clients.put(clientID, new Client(key, level));
		save();
	}
	
	public boolean authClient(String clientID, byte[] key) {
		
		byte[] hashed = hashKey(key);
		byte[] secretHash = getClient(clientID).secretHash;
		
		// a pitiful attempt at an equal time comparison
		boolean equal = true;
		for(int i = 0; i < hashed.length; i++) {
			if(hashed[i] != secretHash[i]) equal = false;
		}
		
		return equal;
	
	}
	
	public List<String> getOutgoingHosts() { return config.outgoingHosts; }
	public int getPort() { return config.port; }
	public AccessLevel getDefaultAccess() { return config.defaultAccessLevel; }
	public AccessLevel getAccess(String clientID) { return getClient(clientID).access; }
	
	// --- classes
	private class ConfigContainer {
		
		public Map<String, Client> clients;
		public List<String> outgoingHosts;
		public transient AccessLevel defaultAccessLevel;
		public int defaultAccess;
		public int port;
		
		public ConfigContainer() {
			clients = new HashMap<String, Client>();
			outgoingHosts = new ArrayList<String>();
			port = 17224; // :)
			defaultAccessLevel = AccessLevel.NONE;
		}
		
		public void afterLoad() {
			defaultAccessLevel = AccessLevel.fromInt(defaultAccess);
			for(Client client: clients.values()) {
				client.complete();
			}
		}
		
	}
	
	private class Client {
		
		public int accessLevel;
		public String keyHash;
		
		public transient byte[] secretHash;
		public transient AccessLevel access;
		
		public Client(byte[] secret, AccessLevel access) {
			this.accessLevel = access.ordinal();
			this.keyHash = Base64.getEncoder().encodeToString(hashKey(secret));
		}
		
		// finish deserialization process
		public void complete() {
			if(keyHash == null) {
				throw new IllegalArgumentException("Client has no key!");
			}
			this.secretHash = Base64.getDecoder().decode(keyHash);
			this.access = AccessLevel.fromInt(accessLevel);
		}
		
	}

}