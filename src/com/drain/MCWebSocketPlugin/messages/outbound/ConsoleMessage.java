package com.drain.MCWebSocketPlugin.messages.outbound;

public class ConsoleMessage extends EventMessage {

	public String threadName;
	public String level;
	public String message;
	public String className;
	
	public ConsoleMessage(String threadName, String level, String message, String className) {
		super("console");
		this.threadName = threadName;
		this.level = level;
		this.message = message;
		this.className = className;
	}
	
}