package com.kisman.cc.gui.api

import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.util.getModifiedWidth

/**
 * @author _kisman_
 * @since 19:13 of 16.02.2023
 */
abstract class ComponentImplementation(
    override var x : Int,
    y0 : Int,
    override var count : Int,
    open var offset : Int,
    override var layer : Int
) : Component {
    override val width
        get() = getModifiedWidth(layer, HalqGui.width)

    override var y = y0
        set(value) { field = value + offset }

    override fun setOff(
        newOff : Int
    ) {
        this.offset = newOff
    }

    override fun updateComponent(
        x : Int,
        y : Int
    ) {
        this.x = x
        this.y = y
    }

    open fun isMouseOnButton(
        x : Int,
        y : Int
    ) : Boolean = x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height
}