package com.kisman.cc.gui.halq.components.sub

import com.kisman.cc.features.module.client.GuiModule
import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.api.Openable
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.util.getModifiedWidth
import com.kisman.cc.gui.halq.util.getXOffset
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d

@Suppress("SENSELESS_COMPARISON", "PrivatePropertyName")
class GroupButton(
        val setting : SettingGroup,
        var x_ : Int,
        var y : Int,
        var offset : Int,
        var count_ : Int,
        var layer_ : Int
) : Openable {
    val comps : ArrayList<Component> = ArrayList()

    private var width_ : Int = HalqGui.width

    var open : Boolean = false

    init {
        width_ = getModifiedWidth(layer_, width_)

        if(setting.settings.isNotEmpty()) {
            var offsetY = offset + HalqGui.height
            var count1 = 0

            for (setting_ in setting.settings) {
                if (setting_ == null) continue
                if (setting_.isGroup && setting_ is SettingGroup) {
                    comps.add(GroupButton(setting_, x, y, offsetY, count1++, layer_ + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isCombo) {
                    comps.add(ModeButton(setting_, x, y, offsetY, count1++, layer_ + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isSlider) {
                    comps.add(Slider(setting_, x, y, offsetY, count1++, layer_ + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isCheck) {
                    comps.add(CheckBox(setting_, x, y, offsetY, count1++, layer_ + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isBind) {
                    comps.add(BindButton(setting_, x, y, offsetY, count1++, layer_ + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isColorPicker) {
                    comps.add(ColorButton(setting_, x, y, offsetY, count1++, layer_ + 1))
                    offsetY += HalqGui.height
                }
            }
        }
    }

    override fun isOpen(): Boolean {
        return open
    }

    override fun getComponents(): ArrayList<Component> {
        return comps
    }

    override fun drawScreen(mouseX: Int, mouseY: Int) {
        Render2DUtil.drawRectWH(
            x.toDouble(),
            (y + offset).toDouble(),
            width_.toDouble(),
            HalqGui.height.toDouble(),
            HalqGui.backgroundColor.rgb
        )
        if (HalqGui.shadowCheckBox) {
            Render2DUtil.drawAbstract(
                    AbstractGradient(
                            Vec4d(
                                    doubleArrayOf(
                                            x.toDouble() + HalqGui.offsets,
                                            (y + offset).toDouble() + HalqGui.offsets
                                    ),
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset).toDouble() + HalqGui.offsets
                                    ),
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset + HalqGui.height).toDouble() - HalqGui.offsets
                                    ),
                                    doubleArrayOf(
                                            x.toDouble() + HalqGui.offsets,
                                            (y + offset + HalqGui.height).toDouble() - HalqGui.offsets
                                    )
                            ),
                            ColorUtils.injectAlpha(HalqGui.backgroundColor.rgb, GuiModule.instance.idkJustAlpha.valInt),
                            HalqGui.getGradientColour(count).color
                    )
            )
            Render2DUtil.drawAbstract(
                    AbstractGradient(
                            Vec4d(
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset).toDouble() + HalqGui.offsets
                                    ),
                                    doubleArrayOf(
                                            (x + width_).toDouble() - HalqGui.offsets,
                                            (y + offset).toDouble() + HalqGui.offsets
                                    ),
                                    doubleArrayOf(
                                            (x + width_).toDouble() - HalqGui.offsets,
                                            (y + offset + HalqGui.height).toDouble() - HalqGui.offsets
                                    ),
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset + HalqGui.height).toDouble() - HalqGui.offsets
                                    )
                            ),
                            HalqGui.getGradientColour(count).color,
                            ColorUtils.injectAlpha(HalqGui.backgroundColor.rgb, GuiModule.instance.idkJustAlpha.valInt)
                    )
            )
        } else Render2DUtil.drawRectWH(
                x.toDouble() + HalqGui.offsets,
                (y + offset).toDouble() + HalqGui.offsets,
                width_.toDouble() - HalqGui.offsets * 2,
                height.toDouble() - HalqGui.offsets * 2,
                HalqGui.getGradientColour(count).rgb
        )

        HalqGui.drawString("${setting.title}...", x, y + offset, width_, HalqGui.height)

        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.drawScreen(mouseX, mouseY)
                }
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if(isMouseOnButton(mouseX, mouseY)) {
            if(button == 0) {
                open = !open
            }
        } else if(isMouseOnButton2(mouseX, mouseY)) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.mouseClicked(mouseX, mouseY, button)
                }
            }
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if(comps.isNotEmpty()) {
            for(comp in comps) {
                if(!comp.visible()) continue
                comp.mouseReleased(mouseX, mouseY, mouseButton)
            }
        }
    }

    override fun updateComponent(x: Int, y: Int) {
        this.x_ = x
        this.y = y
        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.updateComponent(
                            (x - getXOffset(layer)) + getXOffset(comp.layer),
                            y
                    )
                }
            }
        }
    }

    override fun keyTyped(typedChar: Char, key: Int) {
        if(comps.isNotEmpty()) {
            for(comp in comps) {
                if(!comp.visible()) continue
                comp.keyTyped(typedChar, key)
            }
        }
    }

    override fun setOff(newOff: Int) {
        this.offset = newOff
    }

    override fun setCount(count: Int) {
        this.count_ = count
    }

    override fun getHeight(): Int {
        return HalqGui.height
    }

    private fun getHeight1() : Int {
        var height = HalqGui.height
        if(open && comps.isNotEmpty()) {
            height = doIterationFullHeight(comps, HalqGui.height)
        }
        return height
    }

    private fun doIterationFullHeight(components : ArrayList<Component>, oldHeight : Int) : Int {
        var height = oldHeight
        for(component in components) {
            height += component.height
            if(component is Openable) {
                height = doIterationFullHeight(component.getComponents(), height)
            }
        }
        return height
    }

    override fun getCount(): Int {
        return count_
    }

    override fun setWidth(width: Int) {
        this.width_ = width
    }

    override fun setX(x: Int) {
        this.x_ = x
    }

    override fun getX(): Int {
        return this.x_
    }

    override fun setLayer(layer: Int) {
        this.layer_ = layer
    }

    override fun getLayer(): Int {
        return this.layer_
    }

    override fun visible(): Boolean {
        return setting.isVisible
    }

    private fun isMouseOnButton(x: Int, y: Int): Boolean {
        return x > x_ && x < x_ + width_ && y > this.y + offset && y < this.y + offset + HalqGui.height
    }

    private fun isMouseOnButton2(x: Int, y: Int): Boolean {
        return x > x_ && x < x_ + width_ && y > this.y + offset && y < this.y + offset + getHeight1()
    }
}