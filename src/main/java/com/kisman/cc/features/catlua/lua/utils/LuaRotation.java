package com.kisman.cc.features.catlua.lua.utils;

import com.kisman.cc.util.*;
import com.kisman.cc.util.world.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;

public class LuaRotation implements Globals {
    boolean nullCheck(){
        return mc.player == null || mc.world == null;
    }
    public void client(float yaw, float pitch) {
        if(nullCheck()) {
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
        }
    }

    public void packet(float yaw, float pitch, boolean ground) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, ground));
    }

    public void packet(float yaw, float pitch) {
        packet(yaw, pitch, mc.player.onGround);
    }

    public float[] getRotationToEntity(Entity entity) {
        return RotationUtils.getRotation(entity);
    }
}
