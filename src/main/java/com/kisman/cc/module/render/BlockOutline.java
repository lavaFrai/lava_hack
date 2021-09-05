package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockOutline extends Module {
    float[] color = new float[] {0.78f, 0.62f, 0.88f, 1f};

    public BlockOutline() {
        super("BlockOutline", "BlockOutline", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if(mc.objectMouseOver == null) {
            return;
        }
        if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            Block block = BlockUtils.getBlock(mc.objectMouseOver.getBlockPos());
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();

            if (Block.getIdFromBlock(block) == 0) {
                return;
            }
            RenderUtil.drawBlockESP(
                    blockPos,
                    this.color[0],
                    this.color[1],
                    this.color[2]
            );
        }
    }
}
