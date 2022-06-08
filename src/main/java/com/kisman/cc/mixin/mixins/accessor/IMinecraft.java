package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

@Mixin(Minecraft.class)
public interface IMinecraft {

	@Accessor("session")
	void setSession(Session session);

	@Accessor("rightClickDelayTimer")
	void setRightClickDelayTimer(int rightClickDelayTimer);

	@Accessor("timer")
	Timer getTimer();
    @Accessor("metadataSerializer_")
	MetadataSerializer getMetadataSerializer();
}
