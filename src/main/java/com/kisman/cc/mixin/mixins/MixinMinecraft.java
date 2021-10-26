 package com.kisman.cc.mixin.mixins;

 import com.kisman.cc.file.SaveConfig;
 import net.minecraft.crash.CrashReport;
 import org.spongepowered.asm.mixin.Mixin;

 import net.minecraft.client.Minecraft;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 @Mixin(Minecraft.class)
 public class MixinMinecraft {
     @Inject(method = "crashed", at = @At("HEAD"))
     private void crashed(CrashReport crash, CallbackInfo ci) {
         SaveConfig.init();
     }

     @Inject(method = "shutdown", at = @At("HEAD"))
     private void shutdown(CallbackInfo ci) {
         SaveConfig.init();
     }
 }
