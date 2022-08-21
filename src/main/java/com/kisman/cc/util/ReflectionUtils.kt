package com.kisman.cc.util

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @author _kisman_
 * @since 14:16 of 27.07.2022
 */
@Throws(NoSuchFieldException::class, IllegalAccessException::class)
fun setFinalField(
    field : Field,
    `object` : Any?,
    value : Any
) {
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
    field[`object`] = value
}

@Throws(NoSuchFieldException::class, IllegalAccessException::class)
fun setFinalStaticField(
    field : Field,
    value : Any
) {
    setFinalField(field, null, value)
}

@Throws(NoSuchFieldException::class)
fun getField(
    clazz : Class<*>,
    vararg mappings : String?
) : Field? {
    for (s in mappings) {
        try {
            return clazz.getDeclaredField(s)
        } catch (ignored : NoSuchFieldException) { }
    }
    throw NoSuchFieldException(
        "No Such field: " + clazz.name + "-> " + mappings.contentToString()
    )
}