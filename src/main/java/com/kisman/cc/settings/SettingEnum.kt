package com.kisman.cc.settings

import com.kisman.cc.features.module.Module
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 12:58 of 04.08.2022
 */
@Suppress("UNCHECKED_CAST")
class SettingEnum<T : Enum<*>>(
    name : String,
    module : Module,
    t : T
) : Setting(
    name,
    module,
    t
) {
    override fun getValEnum() : T {
        return super.getValEnum() as T
    }

    override fun setTitle(title : String) : SettingEnum<T> {
        return super.setTitle(title) as SettingEnum<T>
    }

    override fun setVisible(visible : Supplier<Boolean>) : SettingEnum<T> {
        return super.setVisible(visible) as SettingEnum<T>
    }

    fun register() : SettingEnum<T> {
        return super.parent.register(this) as SettingEnum<T>
    }
}