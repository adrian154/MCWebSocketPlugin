package com.drain.MCWebSocketPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.drain.MCWebSocketPlugin.commands.ReloadCommand;
import com.google.gson.Gson;

public class MCWebSocketPlugin extends JavaPlugin {

	private Configuration configuration;
	private EventListener listener;
	private WSServer wsServer;
	private Gson gson;
	
	@Override
	public void onEnable() {
		
		initConfig();
		initWSServer();
		initEventListener();
		initLogger();
		initCommands();
		
		/* Set up common Gson instance */
		gson = new Gson();
		
		System.out.println("Hello from MCWebSocket.");
		
	}
	
	private void initConfig() {
		try {
			configuration = new Configuration();
		} catch(IOException exception) {
			System.out.println("Failed to load configuration file: " + exception.getMessage());
			System.out.println("The plugin will function normally, but no applications will be able to authenticate or subscribe to authorized events.");
		}
	}
	
	private void initEventListener() {
		this.listener = new EventListener(this);
		this.getServer().getPluginManager().registerEvents(this.listener, this);
	}
	
	private void initWSServer() {
		wsServer = new WSServer(new InetSocketAddress(1738), this);
		wsServer.start();
	}
	
	private void initLogger() {
		Logger logger = (Logger)LogManager.getRootLogger();
		logger.addAppender(new CustomAppender(this));
	}
	
	private void initCommands() {
		this.getCommand("wsreload").setExecutor(new ReloadCommand(this));
	}
	
	@Override
	public void onDisable() {
		System.out.println("Bye-bye from MCWebSocket.");
		try {
			this.wsServer.stop();
		} catch(IOException | InterruptedException exception) {
			System.out.println("Failed to stop websocket server: " + exception.getMessage());
		}
	}
	
	public WSServer getWSServer() {
		return wsServer;
	}

	public Gson getGson() {
		return gson;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
}