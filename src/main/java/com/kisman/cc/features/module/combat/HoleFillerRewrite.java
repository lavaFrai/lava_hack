package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.*;
import com.kisman.cc.features.module.combat.holefillerrewrite.HolesList;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.features.subsystem.subsystems.TargetsNearest;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.settings.util.SlideRenderingRewritePattern;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.HandModes;
import com.kisman.cc.util.render.pattern.SlideRendererPattern;
import com.kisman.cc.util.world.BlockUtil2;
import com.kisman.cc.util.world.HoleUtil;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/**
 * Work in progress
 * @author Cubic
 */
@Targetable
@TargetsNearest
@ModuleInfo(
        name = "HoleFillerRewrite",
        display = "HoleFiller",
        desc = "Fills holes around you",
        category = Category.COMBAT,
        pingbypass = true
)
public class HoleFillerRewrite extends ShaderableModule {
    @ModuleInstance
    public static HoleFillerRewrite instance;
    
    private final SettingGroup logic = register(new SettingGroup(new Setting("Logic", this)));
    private final SettingGroup render_ = register(new SettingGroup(new Setting("Render", this)));

    private final SettingGroup holesGroup = register(logic.add(new SettingGroup(new Setting("Holes", this))));
    private final Setting obsidianHoles = register(holesGroup.add(new Setting("ObsidianHoles", this, true).setTitle("Obby")));
    private final Setting bedrockHoles = register(holesGroup.add(new Setting("BedrockHoles", this, true).setTitle("Bebrock")));
    private final Setting singleHoles = register(holesGroup.add(new Setting("SingleHoles", this, true).setTitle("1x1")));
    private final Setting doubleHoles = register(holesGroup.add(new Setting("DoubleHoles", this, true).setTitle("2x1")));
    private final Setting customHoles = register(holesGroup.add(new Setting("CustomHoles", this, true).setTitle("Custom")));
    private final Setting blocks = register(logic.add(new Setting("Blocks", this, "Obsidian", Arrays.asList("Obsidian", "EnderChest"))));
    private final Setting swap = register(logic.add(new Setting("Switch", this, "Silent", Arrays.asList("None", "Vanilla", "Normal", "Packet", "Silent"))));
    private final Setting rotate = register(logic.add(new Setting("Rotate", this, false)));
    private final Setting packet = register(logic.add(new Setting("Packet", this, false)));
    private final SettingEnum<HandModes> hand = register(logic.add(new SettingEnum<>("Hand", this, HandModes.MainHand)));
    private final Setting raytrace = register(logic.add(new Setting("RayTrace", this, true)));
    private final Setting place = register(logic.add(new Setting("Place", this, "Instant", Arrays.asList("Instant", "Tick", "Delay"))));
    private final Setting entityCheck = register(logic.add(new Setting("Entity Check", this, false)));
    private final Setting delay = register(logic.add(new Setting("Delay", this, 50, 0, 500, NumberType.TIME).setVisible(() -> place.getValString().equals("Delay"))));
    private final Setting placeMode = register(logic.add(new Setting("PlaceMode", this, "All", Arrays.asList("All", "Target"))));
    private final Setting aroundEnemyRange = register(logic.add(new Setting("TargetHoleRange", this, 4, 1, 10, false).setVisible(() -> placeMode.getValString().equals("Target"))));
    private final Setting holeRange = register(logic.add(new Setting("HoleRange", this, 5, 1, 10, false)));
    private final Setting limit = register(logic.add(new Setting("Limit", this, 0, 0, 50, true)));

    private final SlideRenderingRewritePattern pattern = new SlideRenderingRewritePattern(this).group(render_).preInit().init();

    private final MultiThreaddableModulePattern threads = threads();

    private final SlideRendererPattern renderer = new SlideRendererPattern();

    @Target
    public Entity entity = null;

    public HoleFillerRewrite(){
        super.setDisplayInfo(() -> "[" + (entity == null ? "no target no fun" : ((entity != mc.player ? entity.getName() : "Self"))) + "]");
    }

    private List<BlockPos> holes = new ArrayList<>();

    private final TimerUtils placeTimer = new TimerUtils();

    private final Set<BlockPos> placed = new HashSet<>(512);

    private int lim = 0;

    private BlockPos placePos;

    @Override
    public void onEnable() {
        super.onEnable();
        threads.reset();
        renderer.reset();
        placePos = null;
    }

    @Override
    public void update() {
        if(mc.world == null || mc.player == null) return;

        try {
            entity = placeMode.getValString().equals("All") ? mc.player : EnemyManagerKt.nearest();

            if (entity == null) return;

            threads.update(() -> mc.addScheduledTask(() -> holes = getHoleBlocks(entity)));

            placeHoleBlocks(entity);
        } catch(Exception ignored) {
            System.out.println("eskid moment lmao");
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        handleDraw(pattern);
    }

    @Override
    public void draw() {
        if(place.checkValString("Instant")) for (BlockPos hole : holes) renderer.handleRenderWorld(pattern, hole, null);
        else renderer.handleRenderWorld(pattern, placePos, null);
    }

    private void placeHoleBlocks(Entity entity){
        int slot = getBlockSlot();
        if(slot == -1) return;
        if(place.getValString().equals("Instant")) holes.forEach(blockPos -> place(blockPos, slot));
        else if(place.getValString().equals("Tick")) placeHoleBlocksChained(entity, slot);
        else if(place.getValString().equals("Delay") && placeTimer.passedMillis(delay.getValInt())) placeHoleBlocksChained(entity, slot);

        placeTimer.reset();
    }

    private void placeHoleBlocksChained(Entity entity, int slot){
        boolean clear = true;
        for(BlockPos pos : holes){
            if(placed.contains(pos)) continue;
            if(!mc.world.getEntitiesWithinAABBExcludingEntity(null, mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)).isEmpty()) continue;
            placePos = pos;
            place(pos, slot);
            placed.add(pos);
            clear = false;
            break;
        }
        if(clear) placed.clear();
    }

    private List<BlockPos> getHoleBlocks(Entity entity){
        HolesList holes = new HolesList();
        float range = entity.equals(mc.player) ? holeRange.getValFloat() : aroundEnemyRange.getValFloat();
        Set<BlockPos> possibleHoles = getPossibleHoles(entity, range);
        lim = 0;
        if(singleHoles.getValBoolean()) holes.addPosses(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.SINGLE), entityCheck.getValBoolean());
        if(doubleHoles.getValBoolean()) holes.addPosses(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.DOUBLE), entityCheck.getValBoolean());
        if(customHoles.getValBoolean()) holes.addPosses(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.CUSTOM), entityCheck.getValBoolean());
        return holes;
    }

    private List<BlockPos> getHoleBlocksOfType(Set<BlockPos> possibleHoles, HoleUtil.HoleType type){
        List<BlockPos> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            if(limit.getValInt() != 0 && lim > limit.getValInt())
                break;
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != type)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && bedrockHoles.getValBoolean()){
                List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
                holes.addAll(blocks);
                lim++;
                continue;
            }
            if(!obsidianHoles.getValBoolean())
                continue;
            List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
            holes.addAll(blocks);
            lim++;
        }
        return holes;
    }

    /*
    private List<BlockPos> getSingleHoleBlocks(Set<BlockPos> possibleHoles){
        List<BlockPos> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != HoleUtil.HoleType.SINGLE)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && !bedrockHoles.getValBoolean())
                continue;
            if(!obsidianHoles.getValBoolean())
                continue;

            List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
            holes.addAll(blocks);
        }
        return holes;
    }

    private List<BlockPos> getDoubleHoleBlocks(Set<BlockPos> possibleHoles){
        List<BlockPos> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != HoleUtil.HoleType.DOUBLE)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && !bedrockHoles.getValBoolean())
                continue;
            if(!obsidianHoles.getValBoolean())
                continue;

            List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
            holes.addAll(blocks);
        }
        return holes;
    }
     */

    private Set<BlockPos> getPossibleHoles(Entity entity, float range){
        Set<BlockPos> possibleHoles = new HashSet<>();
        List<BlockPos> blockPosList = WorldUtilKt.sphere(entity, (int) range);
        for (BlockPos pos : blockPosList) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos);
            if(!mc.world.getEntitiesWithinAABB(Entity.class, aabb).isEmpty())
                continue;
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                possibleHoles.add(pos);
        }
        return possibleHoles;
    }

    private BlockPos getEntityPos(Entity entity){
        return new BlockPos(entity.posX, entity.posY, entity.posZ);
    }

    private List<BlockPos> splitAABB(AxisAlignedBB aabb){
        List<BlockPos> list = new ArrayList<>();
        double xDiff = aabb.maxX - aabb.minX;
        double zDiff = aabb.maxZ - aabb.minZ;
        if(xDiff > 2.0 && zDiff > 2.0)
            return list;
        if(xDiff > zDiff){
            int x = (int) aabb.minX;
            int lim = (int) aabb.maxX;
            for(; x < lim; x++){
                list.add(new BlockPos(x, (int) aabb.minY, (int) aabb.minZ));
            }
        } else {
            int z = (int) aabb.minZ;
            int lim = (int) aabb.maxZ;
            for(; z < lim; z++){
                list.add(new BlockPos((int) aabb.minX, (int) aabb.minY, z));
            }
        }
        return list;
    }

    private int getBlockSlot(){
        if(blocks.getValString().equals("Obsidian")){
            return InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        } else {
            return InventoryUtil.getBlockInHotbar(Blocks.ENDER_CHEST);
        }
    }

    private void place(BlockPos pos, int slot){
        if(mc.player == null || mc.player.inventory == null) return;
        int oldSlot = mc.player.inventory.currentItem;
        doSwitch(slot, false);
        BlockUtil2.placeBlock(pos, hand.getValEnum().getHand(), packet.getValBoolean(), raytrace.getValBoolean(), rotate.getValBoolean());
        doSwitch(oldSlot, true);
        mc.playerController.updateController();
    }

    private void doSwitch(int slot, boolean swapBack){
        switch(swap.getValString()){
            case "None":
                break;
            case "Vanilla":
                if(swapBack)
                    break;
                mc.player.inventory.currentItem = slot;
                break;
            case "Normal":
                mc.player.inventory.currentItem = slot;
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                break;
            case "Silent":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                break;
        }
    }
}
