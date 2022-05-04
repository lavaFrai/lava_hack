package com.kisman.cc.mixin.mixins;

import com.google.common.base.Predicate;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventOrientCamera;
import com.kisman.cc.event.events.EventRenderGetEntitiesINAABBexcluding;
import com.kisman.cc.event.events.EventSetupFog;
import com.kisman.cc.event.events.EventUpdateLightmap;
import com.kisman.cc.module.render.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EntityRenderer.class, priority = 10000)
public class MixinEntityRenderer {
    @Mutable
    @Shadow @Final public int[] lightmapColors;

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo ci) {
        EventSetupFog event = new EventSetupFog();
        event.post();
        if(event.isCancelled()) ci.cancel();
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        EventRenderGetEntitiesINAABBexcluding event = new EventRenderGetEntitiesINAABBexcluding();
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) return new ArrayList<>();
        else return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurt(float particalTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.hurtCam.getValBoolean()) ci.cancel();
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void skylightFix(float partialTicks, CallbackInfo ci) {
        EventUpdateLightmap event = new EventUpdateLightmap.Pre();
        event.post();
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "updateLightmap", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE ))
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        EventUpdateLightmap.Post event = new EventUpdateLightmap.Post(lightmapColors);
        event.post();
        if(event.isCancelled()) lightmapColors = event.getLightmapColors();
    }

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"), expect = 0)
    private RayTraceResult rayTraceBlocks(WorldClient worldClient, Vec3d start, Vec3d end) {
        EventOrientCamera event = new EventOrientCamera();
        event.post();
        return event.isCancelled() ? null : worldClient.rayTraceBlocks(start, end);
    }
}
