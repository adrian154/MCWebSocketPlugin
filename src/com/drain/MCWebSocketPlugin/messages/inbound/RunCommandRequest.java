package com.drain.MCWebSocketPlugin.messages.inbound;

import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.Configuration.AccessLevel;
import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;
import com.drain.MCWebSocketPlugin.messages.outbound.ErrorResponse;
import com.drain.MCWebSocketPlugin.messages.outbound.Response;

public class RunCommandRequest extends Request {

	private String command;
	
	@Override
	public Response handle(MCWebSocketPlugin plugin, WebSocket socket) {
		
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
