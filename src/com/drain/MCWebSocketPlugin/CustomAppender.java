package com.drain.MCWebSocketPlugin;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.drain.MCWebSocketPlugin.messages.ConsoleMessage;

@Plugin(name="MCWebSocketAppender", category=Core.CATEGORY_NAME, elementType=Appender.ELEMENT_TYPE)
public class CustomAppender extends AbstractAppender {

	private MCWebSocketPlugin plugin;
	
	public CustomAppender(MCWebSocketPlugin plugin) {
		
		super(
			"MCWebSocketAppender",
			null,
			PatternLayout.newBuilder().withPattern("%msg").build(),
			false
		);
		
		this.plugin = plugin;
		
	}
	
	@Override
	public void append(LogEvent event) {	
		event = event.toImmutable();
		plugin.getWSServer().broadcastMessageToAuthed(new ConsoleMessage(event.getThreadName(), event.getLevel().toString(), event.getMessage().getFormattedMessage()));
	}
	
	@Override
	public boolean isStarted() {
		return true;
	}
	
}
