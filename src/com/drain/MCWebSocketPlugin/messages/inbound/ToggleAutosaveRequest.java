package com.drain.MCWebSocketPlugin.messages.inbound;

import org.bukkit.World;
import org.java_websocket.WebSocket;

import com.drain.MCWebSocketPlugin.MCWebSocketPlugin;
import com.drain.MCWebSocketPlugin.messages.outbound.Response;

public class ToggleAutosaveRequest extends Request {
 
	private boolean state;
	
	@Override
	public Response handle(MCWebSocketPlugin plugin, WebSocket socket) {
		for(World world: plugin.getServer().getWorlds()) {
			world.setAutoSave(state);
		}
		return new Response(this);
	}
	
}
