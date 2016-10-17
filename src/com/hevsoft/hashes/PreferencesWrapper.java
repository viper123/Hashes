package com.hevsoft.hashes;

import java.util.prefs.Preferences;

public class PreferencesWrapper {
	
	private static final Preferences preferences;
	static{
		preferences = Preferences.userNodeForPackage(MainFrame.class);
	}
	private static final String LAST_PATH = "last_path";
	private static final String LAST_ALG = "last_path";

	public String getLastPath(){
		return preferences.get(LAST_PATH, null);
	}
	
	public void setLastPath(String path){
		preferences.put(LAST_PATH, path);
	}
	
	public int getLastAlg(){
		return preferences.getInt(LAST_ALG, -1);
	}
	
	public void setLastAlg(int alg){
		preferences.putInt(LAST_ALG, alg);
	}
}
