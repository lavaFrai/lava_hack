package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.ArrayList;
import com.kisman.cc.hud.hudmodule.Coord;
import com.kisman.cc.hud.hudmodule.Fps;
import com.kisman.cc.hud.hudmodule.Logo;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
	ArrayList arrList;
	Coord coord;
	Fps fps;
	Logo logo;

	public HUD() {
		super("HUD", "hud editor", Category.CLIENT);
		Kisman.instance.settingsManager.rSetting(new Setting("ArrayList", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("Coords", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("FPS", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("Logo", this, false));
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		boolean arrList = Kisman.instance.settingsManager.getSettingByName(this, "ArrayList").getValBoolean();
		boolean coord = Kisman.instance.settingsManager.getSettingByName(this, "Coords").getValBoolean();
		boolean fps = Kisman.instance.settingsManager.getSettingByName(this ,"FPS").getValBoolean();
		boolean logo = Kisman.instance.settingsManager.getSettingByName(this, "Logo").getValBoolean();
		if(arrList) {
			this.arrList = new ArrayList();
		}
		if(logo) {
			this.logo = new Logo(Kisman.NAME, Kisman.VERSION);
		}
		if(fps) {
			this.fps = new Fps();
		}
		if(coord) {
			this.coord = new Coord();
		}
	}
}
