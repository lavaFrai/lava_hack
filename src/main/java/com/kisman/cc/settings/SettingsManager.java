package com.kisman.cc.settings;

import java.util.*;

import com.kisman.cc.module.Module;
import com.kisman.cc.settings.types.SettingGroup;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit him
 *
 *  @author HeroCode
 */
public class SettingsManager {
	
	private final ArrayList<Setting> settings;

	public SettingsManager(){
		this.settings = new ArrayList<>();
	}

	public void rSetting(Setting in){
		this.settings.add(in);
	}

	public ArrayList<Setting> getSettings(){
		return this.settings;
	}
	
	public ArrayList<Setting> getSettingsByMod(Module mod) {
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : getSettings()) if(s.getParentMod() == mod) out.add(s);
		if(out.isEmpty()) return null;
		return out;
	}

	public Setting getSettingByName(Module mod, String name){
		for(Setting set : getSettings()) {
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod) return set;
		}
		return null;
	}

	public Setting getSettingByName(Module mod, String name, boolean ignoreGroups) {
		for(Setting set : getSettings()) {
			if(ignoreGroups && (set.isGroup() || set instanceof SettingGroup)) continue;
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod) return set;
		}
		return null;
	}
}