package com.drain.MCWebSocketPlugin;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

public class MCWebSocketPlugin extends JavaPlugin {

	private EventListener listener;
	private WSServer wsServer;
	private Gson gson;
	
	@Override
	public void onEnable() {
		
		/* Create and register event listener */
		this.listener = new EventListener(this);
		this.getServer().getPluginManager().registerEvents(this.listener, this);
		
		/* Set up server */
		wsServer = new WSServer(new InetSocketAddress(1738), this);
		wsServer.start();

		/* Set up logger */
		initLogger();
		
		/* Set up common Gson instance */
		gson = new Gson();
		
		System.out.println("Hello from MCWebSocket.");
		
	}
	
	private void initLogger() {
		Logger logger = (Logger)LogManager.getRootLogger();
		logger.addAppender(new CustomAppender(this));
	}
	
	@Override
	public void onDisable() {
		System.out.println("Bye-bye from MCWebSocket.");
	}
	
	public WSServer getWSServer() {
		return this.wsServer;
	}

	public Gson getGson() {
		return gson;
	}
	
}