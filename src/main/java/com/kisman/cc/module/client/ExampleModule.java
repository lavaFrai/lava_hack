package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventWorldRender;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
//import com.kisman.cc.oldclickgui.component.components.sub.ColorPicker;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import com.kisman.cc.oldclickgui.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.Color;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExampleModule extends Module {
    public ExampleModule() {
        super("ExampleModule", "example", Category.CLIENT);
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory", this, 1, "ExampleCategory"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCCheckBox", this, 1, "ExampleCCheckBox", false));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory1", this, 2, "ExampleCategory1"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCLine1", this, "ExampleCLine1", 2));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleColorPicker", this, "ExampleColorPicker", false));
    }

    // @SubscribeEvent
    // public void onRender(EventWorldRender event) {
    //     RenderUtil.drawLine(1, 1, 100, 100, 10f, 0xFFFFFF);
    // }

    public void onEnable() {
        //ClickGui.
        //this.subcomponents.add(new ColorPicker(s, this, opY, ));
        //ColorPicker colorPicker = new ColorPicker(5, 85 / 2, Color.WHITE, this::setColor);
        //colorPicker.draw(Mouse.getX(), Display.getHeight() - Mouse.getY());
        //ColorPicker colorPicker = new ColorPicker();
        //mc.displayGuiScreen(colorPicker);//Kisman.instance.colorPicker
    }
}
