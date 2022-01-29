package com.drain.bitcraft;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.drain.MCWebSocketPlugin.messages.outbound.ConsoleMessage;
import com.drain.bitcraft.Configuration.AccessLevel;

@Plugin(name="MCWebSocketAppender", category=Core.CATEGORY_NAME, elementType=Appender.ELEMENT_TYPE)
public class CustomAppender extends AbstractAppender {

	private BitcraftPlugin plugin;
	
	public CustomAppender(BitcraftPlugin plugin) {
		
		super(
			"MCWebSocketAppender",
			null,
			PatternLayout.newBuilder().withPattern("%msg %M %C").build(),
			false
		);
		
		this.plugin = plugin;
		
	}
	
	@Override
	public void append(LogEvent event) {	
		event = event.toImmutable();
		StackTraceElement element = event.getSource();
		plugin.getWSServer().broadcastMessage(new ConsoleMessage(
			event.getThreadName(),
			event.getLevel().toString(),
			event.getMessage().getFormattedMessage(),
			element == null ? "Unknown" : element.getClassName() + "#" + element.getMethodName() + ":" + element.getLineNumber()
		), AccessLevel.CONSOLE_READONLY);
	}
	
	@Override
	public boolean isStarted() {
		return true;
	}
	
}
