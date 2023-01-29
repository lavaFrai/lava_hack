package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.screen.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class Logo extends ShaderableHudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 255)));
    private final Setting mode = register(new Setting("Mode", this, LogoMode.CSGO));
    private final Setting image = register(new Setting("Image Mode", this, LogoImage.Old).setVisible(() -> mode.checkValString("Image")));
    private final Setting bold = register(new Setting("Bold", this, false));
    private final Setting glow = register(new Setting("Glow", this, false));
    private final Setting glowOffset = register(new Setting("Glow Offset", this, 5, 0, 20, true));
    private final Setting csgoVersion = register(new Setting("CSGO Version", this, false));

    public Logo() {
        super("Logo", "lava-hack on top", false, true, false);
    }

    public void draw() {
        String name = Kisman.getName();
        String version = Kisman.getVersion();

        if(mode.checkValString("Simple")) {
            int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();

            if(glow.getValBoolean()) {
                int glowOffset = this.glowOffset.getValInt();

                preNormalRender = () -> Render2DUtil.drawGlow(1 - glowOffset, 1 - glowOffset, 1 + CustomFontUtil.getStringWidth(name + version) + glowOffset, 1 + CustomFontUtil.getFontHeight() + glowOffset, color);
            }

            shaderRender = () -> drawStringWithShadow((bold.getValBoolean() ? TextFormatting.BOLD : "") + name + " " + TextFormatting.GRAY + version, 1, 1, color);
        } else if(mode.checkValString("CSGO")) {
            String text = name + (csgoVersion.getValBoolean() ? TextFormatting.GRAY + " | " + TextFormatting.RESET + Kisman.getVersion() : "") + TextFormatting.GRAY + " | " + TextFormatting.RESET + mc.player.getName() + TextFormatting.GRAY + " | " + TextFormatting.RESET + (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing()) + " ms" + TextFormatting.GRAY + " | " + TextFormatting.RESET + "FPS " + Minecraft.getDebugFPS();
            int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();
            int x = 3;
            int y = 8;
            int width = 4 + CustomFontUtil.getStringWidth(text);
            int height = 4 + CustomFontUtil.getFontHeight();

            preNormalRender = () -> {
                Gui.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
                Gui.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
                Gui.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
                Gui.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
                Gui.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
                Gui.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
                Gui.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
                Gui.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
                Gui.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
                Gui.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
                Gui.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
                Gui.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
                Gui.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
                Gui.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
                Gui.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
                Gui.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));
            };

            shaderRender = () -> {
                drawStringWithShadow((bold.getValBoolean() ? TextFormatting.BOLD : "") + text, x + 2, y + 2 + (shaderSetting.getValBoolean() ? 1 : 0), color);
                Gui.drawRect(x, y - 5, x + width, y - 4, color);
            };
        } else if(mode.checkValString("GishCode")) {
            int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();

            shaderRender = () -> {
                GL11.glPushMatrix();

                GL11.glScaled(1.5, 1.5, 1.5);
                mc.fontRenderer.drawString("LavaHack", 4, 4, color, !shaderSetting.getValBoolean());
                GL11.glScaled(0.6, 0.6, 0.6);
                mc.fontRenderer.drawString(TextFormatting.GRAY + Kisman.getVersion(), 84, 4, -1, !shaderSetting.getValBoolean());
                mc.fontRenderer.drawString(TextFormatting.GRAY + "1.12.2", 84, 14, -1, !shaderSetting.getValBoolean());

                GL11.glPopMatrix();
            };
        } else {
            shaderRender = () -> {
                if (image.checkValString("Old")) Icons.LOGO.render(0, 0, 50, 50);
                else if (image.checkValString("New")) Icons.LOGO_NEW.render(0, 0, 80, 80, new Colour(ColorUtils.astolfoColors(100, 100)));
            };
        }
    }

    public enum LogoMode {Simple, CSGO, Image, GishCode}
    public enum LogoImage {Old, New}
}
