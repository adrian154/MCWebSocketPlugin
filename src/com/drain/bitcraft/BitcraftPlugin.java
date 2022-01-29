package com.drain.bitcraft;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.drain.MCWebSocketPlugin.commands.AddClientCommand;
import com.drain.MCWebSocketPlugin.commands.ClearMarkerCommand;
import com.drain.MCWebSocketPlugin.commands.GroupChatCommand;
import com.drain.MCWebSocketPlugin.commands.GroupCommand;
import com.drain.MCWebSocketPlugin.commands.MarkersCommand;
import com.drain.MCWebSocketPlugin.commands.MessageCommand;
import com.drain.MCWebSocketPlugin.commands.ReloadCommand;
import com.drain.MCWebSocketPlugin.commands.SetMarkerCommand;
import com.drain.MCWebSocketPlugin.commands.StatusCommand;
import com.google.gson.Gson;

public class BitcraftPlugin extends JavaPlugin {
	
	public static final String MARKER_SET_NAME = "bitcraftmarkers";
	public static final String MARKER_LAYER_NAME = "Locations";
	
	private Configuration config;
	private EventListener listener;
	private WSServer wsServer;
	private Gson gson;
	private java.util.logging.Logger logger;
	
	private MarkerAPI markerAPI;
	private MarkerSet markerSet;
	private DynmapCommonAPI dynmapAPI;
	
	// --- impled methods
	@Override
	public void onEnable() {
		
		this.dynmapAPI = (DynmapCommonAPI)this.getServer().getPluginManager().getPlugin("dynmap");
		this.markerAPI = dynmapAPI.getMarkerAPI();
		this.markerSet = markerAPI.getMarkerSet(MARKER_SET_NAME);
		if(markerSet == null) {
			this.markerSet = markerAPI.createMarkerSet(MARKER_SET_NAME, MARKER_LAYER_NAME, (Set<MarkerIcon>)null, true);
		}
		
		this.logger = getLogger();
		this.gson = new Gson();
		initConfig();
		initWebsockets();
		initEvents();
		initLogger();
		initCommands();
		logger.info("Hello from MCWebSocket.");
		logger.info("Debug statement goes here");
		
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
	
	// --- private methods
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
		this.getCommand("mcws-status").setExecutor(new StatusCommand(this));
	
		MessageCommand msg = new MessageCommand(this);
		this.getCommand("msg").setExecutor(msg);
		this.getCommand("tell").setExecutor(msg);
		this.getCommand("w").setExecutor(msg);
	
		this.getCommand("setmarker").setExecutor(new SetMarkerCommand(this));
		this.getCommand("markers").setExecutor(new MarkersCommand(this));
		this.getCommand("clearmarker").setExecutor(new ClearMarkerCommand(this));
		
		this.getCommand("g").setExecutor(new GroupCommand(this));
		this.getCommand("gc").setExecutor(new GroupChatCommand(this));
		
	}
	
	// --- public methods
	public WSServer getWSServer() {
		return wsServer;
	}

	public Gson getGson() {
		return gson;
	}
	
	public Configuration config() {
		return config;
	}
	
	// evil code prone to breakage
	public boolean addMarker(Player player, String iconName, String name) {
		Location location = player.getLocation();
		List<Integer> markers = config.getMarkers(player.getUniqueId());
		int id;
		if(markers.size() == 0) {
			id = 0;
		} else {
			id = markers.get(markers.size() - 1) + 1;
		}
		MarkerIcon icon = markerAPI.getMarkerIcon(iconName);
		if(icon == null) {
			return false;
		}
		this.markerSet.createMarker(player.getUniqueId().toString() + "." + id, name, player.getWorld().getName(), location.getX(), location.getY(), location.getZ(), icon, true);
		markers.add(id);
		return true;
	}
	
	public Marker getMarker(UUID uuid, int id) {
		return this.markerSet.findMarker(uuid.toString() + "." + id);
	}
	
}