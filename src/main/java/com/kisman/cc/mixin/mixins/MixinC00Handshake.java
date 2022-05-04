package com.kisman.cc.mixin.mixins;

import com.kisman.cc.event.events.EventC00Handshake;
import net.minecraft.network.*;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C00Handshake.class)
public class MixinC00Handshake {
    @Shadow private int protocolVersion;
    @Shadow private int port;
    @Shadow private String ip;
    @Shadow private EnumConnectionState requestedState;

    @Inject(method = "writePacketData", at = @At(value = "HEAD"), cancellable = true)
    public void writePacketData(PacketBuffer buf, CallbackInfo ci) {
        EventC00Handshake event = new EventC00Handshake(buf);
        event.post();
        if(event.isCancelled()) {
            ci.cancel();
            buf.writeVarInt(protocolVersion);
            buf.writeString(ip);
            buf.writeShort(port);
            buf.writeVarInt(requestedState.getId());
        }
    }
}
