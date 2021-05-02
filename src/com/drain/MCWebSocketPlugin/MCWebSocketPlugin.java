package com.drain.MCWebSocketPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.drain.MCWebSocketPlugin.commands.AddClientCommand;
import com.drain.MCWebSocketPlugin.commands.ReloadCommand;
import com.google.gson.Gson;

public class MCWebSocketPlugin extends JavaPlugin {

	private Configuration config;
	private EventListener listener;
	private WSServer wsServer;
	private Gson gson;
	private java.util.logging.Logger logger;
	
	// --- impled methods
	@Override
	public void onEnable() {
		this.logger = getLogger();
		this.gson = new Gson();
		initConfig();
		initWebsockets();
		initEvents();
		initLogger();
		initCommands();
		logger.info("Hello from MCWebSocket.");
	}
	
	@Override
	public void onDisable() {
		logger.info("Goodbye from MCWebSocket.");
		try {
			this.wsServer.stop();
		} catch(IOException | InterruptedException exception) {
			logger.severe("Something went wrong while shutting down. You should probably look into it.");
			logger.severe(exception.toString());
		}
	}
	
	private void initConfig() throws RuntimeException {
		try {
			config = new Configuration();
		} catch(IOException exception) {
			exception.printStackTrace();
			throw new RuntimeException("Failed to initialize configuration!");
		}
	}
	
	private void initWebsockets() {
		wsServer = new WSServer(new InetSocketAddress(config.getPort()), this);
	}
	
	private void initEvents() {
		this.listener = new EventListener(this);
		this.getServer().getPluginManager().registerEvents(this.listener, this);
	}
	
	private void initLogger() {
		Logger logger = (Logger)LogManager.getRootLogger();
		logger.addAppender(new CustomAppender(this));
	}
	
	private void initCommands() {
		this.getCommand("mcws-reload").setExecutor(new ReloadCommand(this));
		this.getCommand("mcws-addclient").setExecutor(new AddClientCommand(this));
	}
	
	// --- methods
	public WSServer getWSServer() {
		return wsServer;
	}

	public Gson getGson() {
		return gson;
	}
	
	public Configuration getMCWSConfig() {
		return config;
	}
	
}