package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventSpawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinWorld {
    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    private void onEntityAdded(Entity entityIn, CallbackInfo ci) {
        Kisman.EVENT_BUS.post(new EventSpawnEntity(entityIn));
    }
}
