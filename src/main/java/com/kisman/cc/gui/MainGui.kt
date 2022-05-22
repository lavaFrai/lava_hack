package com.kisman.cc.gui

import com.kisman.cc.Kisman
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Render2DUtil
import com.kisman.cc.util.customfont.CustomFontUtil
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse

class MainGui {
    companion object {
        fun openGui(bar : SelectionBar) {
            when (bar.selection) {
                Guis.ClickGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.halqGui)
                Guis.CSGOGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.clickGuiNew)
                Guis.HudEditor -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.halqHudGui)
                Guis.Music -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.musicGui)
                Guis.Console -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.consoleGui)
            }
        }
    }

    class SelectionBar(
            defaultSelection : Guis
    ) {
        var selection : Guis
        val backgroundColor : Colour = Colour(20, 20, 20, 200)
        val offset : Int = 5

        init {
            selection = defaultSelection
        }

        fun drawScreen(mouseX : Int, mouseY : Int) {
            var startX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - getSelectionBarWidth() / 2
            Render2DUtil.drawRectWH(startX.toDouble(), 0.0, getSelectionBarWidth().toDouble(), (CustomFontUtil.getFontHeight() + offset * 2).toDouble(), backgroundColor.rgb)

            for(gui in Guis.values()) {
                CustomFontUtil.drawStringWithShadow(
                        gui.displayName,
                        (startX + offset).toDouble(),
                        offset.toDouble(),
                        if(gui == selection) ColorUtils.astolfoColors(100, 100) else -1
                )
                if(Mouse.isButtonDown(0)) {
                    if(mouseX >= startX && mouseX <= startX + offset * 2 + CustomFontUtil.getStringWidth(gui.displayName) && mouseY >= 0 && mouseY <= offset * 2 + CustomFontUtil.getFontHeight()) {
                        selection = gui
                    }
                }
                startX += offset * 2 + CustomFontUtil.getStringWidth(gui.displayName)
            }
        }

        fun mouseClicked(mouseX : Int, mouseY : Int) : Boolean {
            /*val startX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - getSelectionBarWidth() / 2
            if(mouseX >= startX && mouseX <= startX + getSelectionBarWidth() && mouseY >= 0 && mouseY <= CustomFontUtil.getFontHeight() + offset * 2) {
                for((count, gui) in Guis.values().withIndex()) {
                    if(mouseX >= startX + (count * (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) && mouseX <= startX + (count * (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) + (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) {
                        println("Gui: ${gui.displayName}")
                        selection = gui
                        return false
                    }
                }
            }
            return true*/
            return true
        }

        private fun getSelectionBarWidth() : Int {
            var width = 0

            for(gui in Guis.values()) {
                width += offset * 2 + CustomFontUtil.getStringWidth(gui.displayName)
            }

            return width
        }
    }

    enum class Guis(
            val displayName: String
    ) {
        ClickGui("Click Gui"),
        CSGOGui("CSGO Gui"),
        HudEditor("Hud Editor"),
        Music("Music"),
        Console("Console")
    }
}