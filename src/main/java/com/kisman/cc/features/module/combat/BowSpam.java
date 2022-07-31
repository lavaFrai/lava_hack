package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.BlockPos;

public class BowSpam extends Module {
    private final Setting drawLength = register(new Setting("DrawLength", this, 3, 3, 21, true));

    public BowSpam() {
        super("BowSpam", "", Category.COMBAT);
    }

    public void update() {
        if (mc.player != null && mc.world != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= drawLength.getValDouble()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
    }
}
