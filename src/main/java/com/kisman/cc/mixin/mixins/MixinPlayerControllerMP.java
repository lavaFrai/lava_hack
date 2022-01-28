package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerMP.class, priority = 10000)
public class MixinPlayerControllerMP {
    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    private void clickBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        EventDamageBlock event = new EventDamageBlock(posBlock, directionFacing);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At("RETURN"))
    public void playerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        DestroyBlockEvent event = new DestroyBlockEvent(pos);

        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) cir.setReturnValue(false);
    }
}
