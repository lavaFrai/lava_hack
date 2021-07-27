package com.kisman.cc.module.modules.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Step extends Module {

    public Step() {
        super("Step", "setting your step heigth", Category.MOVEMENT);
        //height = this.register("height", 2.5, 0.5, 2.5);
        Kisman.instance.settingsManager.rSetting(new Setting("Heigth", this, 0.5f, 0.5f, 2.5f, false));
    }

    public void update() {
        float height = (float) Kisman.instance.settingsManager.getSettingByName(this, "Heigth").getValDouble();
        Minecraft.getMinecraft().player.stepHeight = height;
    }

    public void onDisable() {
        Minecraft.getMinecraft().player.stepHeight = 0.5f;
    }
}
