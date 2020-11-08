package com.drain.MCWebSocketPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

	private File configFile;
	private List<String> keys;
	
	public Configuration() throws IOException {
		keys = new ArrayList<String>();
		this.reload();
	}
	
	public void reload() throws IOException {
		
		configFile = new File("MCWebSocket/config.txt");
		configFile.createNewFile();
		keys.removeAll(keys);
		
		/* This part is crude, since I plan on extending it to other configuration options */
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		
		String line;
		while((line = reader.readLine()) != null) {
			keys.add(line);
		}
		
		reader.close();
		
	}
	
	public boolean verifyKey(String key) {
		return keys.contains(key);
	}

}