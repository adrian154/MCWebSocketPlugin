package com.drain.bitcraft;

import org.bukkit.ChatColor;

public final class Colors {

	public static ChatColor fromString(String color) {
		switch(color) {
			case "gray": return ChatColor.GRAY;
			case "blue": return ChatColor.BLUE;
			case "green": return ChatColor.GREEN;
			case "aqua": return ChatColor.AQUA;
			case "red": return ChatColor.RED;
			case "magenta": return ChatColor.DARK_PURPLE;
			case "yellow": return ChatColor.YELLOW;
			case "white": return ChatColor.WHITE;
			default: return null;
		
		}
	}
	
	public static ChatColor getComplementary(ChatColor color) {
		switch(color) {
			case DARK_GRAY: return ChatColor.BLACK;
			case BLUE: return ChatColor.DARK_BLUE;
			case GREEN: return ChatColor.DARK_GREEN;
			case AQUA: return ChatColor.DARK_AQUA;
			case RED: return ChatColor.DARK_RED;
			case LIGHT_PURPLE: return ChatColor.DARK_PURPLE;
			case YELLOW: return ChatColor.GOLD;
			case WHITE: return ChatColor.GRAY;
			default: return null;
		}
	}
	
}
