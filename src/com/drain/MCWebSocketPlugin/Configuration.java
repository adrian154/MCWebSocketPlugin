package com.drain.MCWebSocketPlugin;

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

	public List<String> getOutgoingHosts() { return config.outgoingHosts; }
	public int getPort() { return config.port; }
	public AccessLevel getDefaultAccess() { return config.defaultAccessLevel; }
	public Client getClient(String name) { return config.clients.get(name); }
	public String getServerIDSecret() { return config.serverIDSecret; }
	
	// --- inner classes
	private class ConfigContainer {
		
		public Map<String, Client> clients;
		public List<String> outgoingHosts;
		public transient AccessLevel defaultAccessLevel;
		public int defaultAccess;
		public int port;
		public String serverIDSecret;
		
		public ConfigContainer() {
			
			// defaults
			clients = new HashMap<String, Client>();
			outgoingHosts = new ArrayList<String>();
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

}