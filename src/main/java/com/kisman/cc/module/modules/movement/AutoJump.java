package com.kisman.cc.module.modules.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;

public class AutoJump extends Module {
    public AutoJump() {
        super("AutoJump", "Auto jump", Category.MOVEMENT);
    }

    public void update() {
        Minecraft.getMinecraft().gameSettings.autoJump = true;
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.autoJump = false;
    }
}
