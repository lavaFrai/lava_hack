package com.kisman.cc;

import com.kisman.cc.clickgui.NewClickGUI;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.ModuleManager;
import com.kisman.cc.settings.SettingsManager;
import me.zero.alpine.bus.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.opengl.Display;

public class Kisman
{
    public static final String NAME = "kisman.cc";
    public static final String MODID = "kisman";
    public static final String VERSION = "b1";

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public NewClickGUI clickGUI;
    public ClickGui clickGui;

    //Coord Exploit
    
    public void init() {
        Display.setTitle(NAME + " " + VERSION);
    	MinecraftForge.EVENT_BUS.register(this);
    	settingsManager = new SettingsManager();
    	moduleManager = new ModuleManager();
        clickGUI = new NewClickGUI();
    	clickGui = new ClickGui();

        //Coord Exploit
    }
    
    @SubscribeEvent
    public void key(KeyInputEvent e) {
    	if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
    		return; 
    	try {
             if (Keyboard.isCreated()) {
                 if (Keyboard.getEventKeyState()) {
                     int keyCode = Keyboard.getEventKey();
                     if (keyCode <= 0)
                    	 return;
                     for (Module m : moduleManager.modules) {
                    	 if (m.getKey() == keyCode && keyCode > 0) {
                    		 m.toggle();
                    	 }
                     }
                 }
             }
         } catch (Exception q) { q.printStackTrace(); }
    }
}
