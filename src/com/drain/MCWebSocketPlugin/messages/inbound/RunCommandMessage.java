package com.drain.MCWebSocketPlugin.messages.inbound;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;
import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;

public class RunCommandMessage extends InboundMessage {

	private String command;
	
	@Override
	public void handle(MCWebSocketPlugin plugin, WebSocket socket) {
		if(command != null && plugin.getWSServer().getAccess(socket).allows(AccessLevel.FULL_ACCESS)) {
			plugin.getServer().getScheduler().runTask(plugin, new RunCommandTask(plugin, command));
			socket.send(SUCCESS);
		} else {
			socket.send(INVALID_FIELDS);
		}
	}
	
	private static class RunCommandTask implements Runnable {
		
		private MCWebSocketPlugin plugin;
		private String command;
		
		public RunCommandTask(MCWebSocketPlugin plugin, String command) {
			this.plugin = plugin;
			this.command = command;
		}
		
		@Override
		public void run() {
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
		}
		
	}
	
}
