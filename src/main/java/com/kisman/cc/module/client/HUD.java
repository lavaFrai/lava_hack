package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
//import com.kisman.cc.hud.hudmodule.ArrayList;
// import com.kisman.cc.hud.hudmodule.Coord;
// import com.kisman.cc.hud.hudmodule.Fps;
// import com.kisman.cc.hud.hudmodule.Logo;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LogoMode;

import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.*;

public class HUD extends Module {
	public static HUD instance;

	private Setting arrLine = new Setting("ArrLine", this, "ArrayList");
	public Setting arrMode = new Setting("ArrayListMode", this, "RIGHT", new ArrayList<>(Arrays.asList("LEFT", "RIGHT")));
	public Setting arrY = new Setting("ArrayListY", this, 150, 0, mc.displayHeight, true);
	public Setting arrColor = new Setting("ArrayListColor", this, "Color", new float[] {3f, 0.03f, 0.33f, 1f}, false);

	private Setting welLine = new Setting("WelLine", this, "Welcomer");
	public Setting welColor = new Setting("WelColor", this, "WelcomerColor", new float[] {3f, 0.03f, 0.33f, 1f}, false);

	private Setting pvpLine = new Setting("PvpLine", this, "PvpInfo");
	public Setting pvpY = new Setting("PvpInfoY", this, 200, 0, mc.displayHeight, true);

	private Setting armLine = new Setting("ArmLine", this, "Armor");
	public Setting armOffRender = new Setting("OffHandRender", this, false);
	public Setting armExtra = new Setting("ExtraInfo", this, false);
	public Setting armDmg = new Setting("Damage", this, false);

	private Setting radarLine = new Setting("RadarLine", this, "Radar");
	public Setting radarDist = new Setting("MaxDistance", this, 50, 10, 50, true);
	public Setting radarY = new Setting("RadarY", this, 3 + (CustomFontUtil.getFontHeight() * 2), 0, mc.displayHeight, true);

	private Setting speedLine = new Setting("SpeedLine", this, "Speed");
	public Setting speedMode = new Setting("SpeedMode", this, "km/h", new ArrayList<>(Arrays.asList("b/s", "km/h")));

	public HUD() {
		super("HudEditor", "hud editor", Category.CLIENT);

		instance = this;

		setmgr.rSetting(arrLine);
		setmgr.rSetting(arrMode);
		setmgr.rSetting(arrY);
		setmgr.rSetting(arrColor);

		setmgr.rSetting(welLine);
		setmgr.rSetting(welColor);

		setmgr.rSetting(pvpLine);
		setmgr.rSetting(pvpY);

		setmgr.rSetting(arrLine);
		setmgr.rSetting(armOffRender);
		setmgr.rSetting(armExtra);
		setmgr.rSetting(armDmg);

		setmgr.rSetting(radarLine);
		setmgr.rSetting(radarDist);
		setmgr.rSetting(radarY);

		setmgr.rSetting(speedLine);
		setmgr.rSetting(speedMode);
	}

	public void onEnable() {
		super.onEnable();
        mc.displayGuiScreen(Kisman.instance.hudGui);
		super.setToggled(false);
	}
}
