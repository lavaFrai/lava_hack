package com.kisman.cc.module.chat;

import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.util.text.TextFormatting;

public class Credits extends Module {
    private String msg = "";
    public Credits() {
        super("Credits", Category.CHAT);
    }

    public void onEnable() {
        msg = "\nCredits:\n";

        add("CatClient base", "First ClickGui");
        add("HeroCode", "Settings system");
        add("Vega33", "BowExploitRewrite & shader gui design & Jesus");
        add("TheRealDiOnFire", "Supporter");
        add("kshk", "Charms");
        add("salam4ik", "TeleportBack & glow shader");
        add("fendy", "BowExploit");
        add("Neko+", "ColorPicker");
        add("NekoPvP", "ItemFOV");
        add("cum stealer", "Simple mode from SwingAnimation");
        add("kqllqk", "CustomFont renderer");
        add("NoRules", "New CustomFont renderer from .ttf");
        add("SalHack", "First CA & Scaffold & more utilities files");
        add("ZeroTwo", "Anchor");
        add("sedza", "New logo");
        add("superblauberee27", "New CSGO gui");
        add("Phobos 1.3.1", "more .frag files");
        add("Phobos 1.9.0", "RangeVisualisator");
        add("NEC", "NEC");
        add("RerHack.club", "Help me with AutoRer");
        add("Some contributors", "some modules");

        ChatUtils.simpleMessage(msg);

        setToggled(false);
    }

    public void add(String name, String desc) {
        msg += TextFormatting.LIGHT_PURPLE + name + ": " + TextFormatting.RESET + desc + "\n";
    }
}
