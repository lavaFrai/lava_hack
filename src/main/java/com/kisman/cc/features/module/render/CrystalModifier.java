package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import net.minecraft.entity.item.EntityEnderCrystal;

public class CrystalModifier extends Module {
    public static CrystalModifier instance;

    public Setting mode = new Setting("Mode", this, Modes.Fill);
    public Setting preview = new Setting("Crystal", this, "Crystal", new EntityEnderCrystal(mc.world));

    public Setting scale = new Setting("Scale", this,false);
    public Setting scaleVal = new Setting("ScaleVal", this, 1, 0.1, 2, false);

    public Setting translateX = new Setting("TranslateX", this, 0, -2, 2, false);
    public Setting translateY = new Setting("TranslateY", this, 0, -2, 2, false);
    public Setting translateZ = new Setting("TranslateZ", this, 0, -2, 2, false);

    public Setting insideCube = new Setting("InsideCube", this, true);
    public Setting outsideCube = new Setting("OutsideCube", this, true);
    public Setting outsideCube2 = new Setting("OutsideCube2", this, true);
    public Setting texture = new Setting("Texture", this, false);
    public Setting customColor = new Setting("CustomColor", this, false);
    public Setting crystalColor = new Setting("CrystalColor", this, "Color", new Colour(0, 0, 255));

    public Setting outline = new Setting("Outline", this, false);
    public Setting outlineMode = new Setting("OutlineMode", this, OutlineModes.Wire);
    public Setting lineWidth = new Setting("LineWidth", this, 3, 0.5, 5, false);
    public Setting color = new Setting("Outline Color", this, "Color", new Colour(255, 0, 0));


    public Setting speed = new Setting("CrystalSpeed", this, 3, 0, 50, false);
    public Setting bounce = new Setting("CrystalBounce", this, 0.2f, 0, 10, false);

    public CrystalModifier() {
        super("CrystalCharms", "r", Category.RENDER);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        instance = this;

        setmgr.rSetting(mode);

        setmgr.rSetting(scale);
        setmgr.rSetting(scaleVal);

        setmgr.rSetting(translateX);
        setmgr.rSetting(translateY);
        setmgr.rSetting(translateZ);

        setmgr.rSetting(insideCube);
        setmgr.rSetting(outsideCube);
        setmgr.rSetting(outsideCube2);
        setmgr.rSetting(texture);
        setmgr.rSetting(customColor);
        setmgr.rSetting(crystalColor);

        setmgr.rSetting(outline);
        setmgr.rSetting(outlineMode);
        setmgr.rSetting(lineWidth);
        setmgr.rSetting(color);

        setmgr.rSetting(speed);
        setmgr.rSetting(bounce);
    }

    public enum OutlineModes {Wire, Flat}
    public enum Modes {Fill, Wireframe,}
}
