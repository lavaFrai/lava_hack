package com.kisman.cc.mixin.mixins;

import com.google.common.base.Predicate;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventEntityRender;
import com.kisman.cc.event.events.EventRenderGetEntitiesINAABBexcluding;
import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        EventRenderGetEntitiesINAABBexcluding event = new EventRenderGetEntitiesINAABBexcluding();
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            return new ArrayList<>();
        } else {
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
        }
    }

    @Inject(method = "renderWorldPass", at = @At("HEAD"), cancellable = true)
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        EventEntityRender event = new EventEntityRender(partialTicks, Event.Era.PRE);
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurt(float particalTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.hurtCam.getValBoolean()) {
            ci.cancel();
        }
    }
}
