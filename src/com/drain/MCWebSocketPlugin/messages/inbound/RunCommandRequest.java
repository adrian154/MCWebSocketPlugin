package com.drain.MCWebSocketPlugin.messages.inbound;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.messages.outbound.ErrorResponse;
import com.drain.MCWebSocketPlugin.messages.outbound.Response;
import com.drain.bitcraft.BitcraftPlugin;
import com.drain.bitcraft.Configuration.AccessLevel;

public class RunCommandRequest extends Request {

	private String command;
	
	@Override
	public Response handle(BitcraftPlugin plugin, WebSocket socket) {
		
		if(!plugin.getWSServer().getAccess(socket).contains(AccessLevel.CONSOLE)) { 
			return new ErrorResponse("Not authorized", this);
		}
		
		if(command != null) {
			plugin.getServer().getScheduler().runTask(plugin, new RunCommandTask(plugin, command));
			return new Response(this);
		} else {
			return new ErrorResponse("Missing fields", this);
		}
		
	}
	
	private static class RunCommandTask implements Runnable {
		
		private BitcraftPlugin plugin;
		private String command;
		
		public RunCommandTask(BitcraftPlugin plugin, String command) {
			this.plugin = plugin;
			this.command = command;
		}
		
		@Override
		public void run() {
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
		}
		
	}
	
}
