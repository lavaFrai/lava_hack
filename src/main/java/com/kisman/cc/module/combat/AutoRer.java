package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.ai.autorer.AutoRerAI;
import com.kisman.cc.event.events.*;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.bypasses.SilentSwitchBypass;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoRer extends Module {
    public final Setting lagProtect = new Setting("Lag Protect", this, false);
    public final Setting placeRange = new Setting("Place Range", this, 6, 0, 6, false);
    private final Setting placeWallRange = new Setting("Place Wall Range", this, 4.5f, 0, 6, false);
    private final Setting breakRange = new Setting("Break Range", this, 6, 0, 6, false);
    private final Setting breakWallRange = new Setting("Break Wall Range", this, 4.5f, 0, 6, false);
    private final Setting targetRange = new Setting("Target Range", this, 9, 0, 20, false);
    private final Setting logic = new Setting("Logic", this, LogicMode.PlaceBreak);
    public final Setting terrain = new Setting("Terrain", this, false);
    private final Setting switch_ = new Setting("Switch", this, SwitchMode.None);
    private final Setting fastCalc = new Setting("Fast Calc", this, true);
    private final Setting motionCrystal = new Setting("Motion Crystal", this, false);
    private final Setting motionCalc = new Setting("Motion Calc", this, false);
    private final Setting swing = new Setting("Swing", this, SwingMode.PacketSwing);
    private final Setting instant = new Setting("Instant", this, true);
    private final Setting instantCalc = new Setting("Instant Calc", this, true);
    private final Setting instantRotate = new Setting("Instant Rotate", this, true);
    private final Setting inhibit = new Setting("Inhibit", this, true);
    private final Setting sound = new Setting("Sound", this, true);
    public final Setting syns = new Setting("Syns", this, true);
    private final Setting rotate = new Setting("Rotate", this, Rotate.Place);
    private final Setting rotateMode = new Setting("Rotate Mode", this, RotateMode.Silent);
    private final Setting ai = new Setting("AI", this, false);
    private final Setting calcDistSort = new Setting("Calc Dist Sort", this, false);

    private final Setting placeLine = new Setting("PlaceLine", this, "Place");
    private final Setting place = new Setting("Place", this, true);
    public final Setting secondCheck = new Setting("Second Check", this, false);
    private final Setting thirdCheck = new Setting("Third Check", this, false);
    public final Setting armorBreaker = new Setting("Armor Breaker", this, 100, 0, 100, Slider.NumberType.PERCENT);
    private final Setting multiPlace = new Setting("Multi Place", this, false);
    private final Setting firePlace = new Setting("Fire Place", this, false);
    private final Setting liquidPlace = new Setting("Liquid Place", this, false);

    private final Setting breakLine = new Setting("BreakLine", this, "Break");
    private final Setting break_ = new Setting("Break", this, true);
    private final Setting breakPriority = new Setting("Break Priority", this, BreakPriority.Damage);
    private final Setting friend_ = new Setting("Friend", this, FriendMode.AntiTotemPop);
    private final Setting clientSide = new Setting("Client Side", this, false);
    private final Setting manualBreaker = new Setting("Manual Breaker", this, false);
    private final Setting removeAfterAttack = new Setting("Remove After Attack", this, false);
    private final Setting antiCevBreakerMode = new Setting("Anti Cev Breaker", this, AntiCevBreakerMode.None);

    private final Setting delayLine = new Setting("DelayLine", this, "Delay");
    private final Setting placeDelay = new Setting("Place Delay", this, 0, 0, 2000, Slider.NumberType.TIME);
    private final Setting breakDelay = new Setting("Break Delay", this, 0, 0, 2000, Slider.NumberType.TIME);
    private final Setting calcDelay = new Setting("Calc Delay", this, 0, 0, 20000, Slider.NumberType.TIME);
    private final Setting clearDelay = new Setting("Clear Delay", this, 500, 0, 2000, Slider.NumberType.TIME);
    private final Setting multiplication = new Setting("Multiplication", this, 1, 1, 10, true);

    private final Setting dmgLine = new Setting("DMGLine", this, "Damage");
    public final Setting minDMG = new Setting("Min DMG", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("Max Self DMG", this, 18, 0, 37, true);
    private final Setting maxFriendDMG = new Setting("Max Friend DMG", this, 10, 0, 37, true);
    public final Setting lethalMult = new Setting("Lethal Mult", this, 0, 0, 6, false);

    private final Setting threadLine = new Setting("ThreadLine", this, "Thread");
    private final Setting threadMode = new Setting("Thread Mode", this, ThreadMode.None);
    private final Setting threadDelay = new Setting("Thread Delay", this, 50, 0, 1000, Slider.NumberType.TIME).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()));
    private final Setting threadSyns = new Setting("Thread Syns", this, true).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()));
    private final Setting threadSynsValue = new Setting("Thread Syns Value", this, 1000, 1, 10000, Slider.NumberType.TIME).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()));
    private final Setting threadPacketRots = new Setting("Thread Packet Rots", this, false).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()) && !rotate.checkValString(Rotate.Off.name()));
    private final Setting threadSoundPlayer = new Setting("Thread Sound Player", this, 6, 0, 12, true);

    private final Setting renderLine = new Setting("RenderLine", this, "Render");
    private final Setting render = new Setting("Render", this, Render.Default);
    private final Setting text = new Setting("Text", this, true);
    private final Setting infoMode = new Setting("InfoMode", this, InfoMode.Target);

    private final Setting red = new Setting("Red", this, 1, 0, 1, false);
    private final Setting green = new Setting("Green", this, 0, 0, 1, false);
    private final Setting blue = new Setting("Blue", this, 0, 0, 1, false);
    private final Setting alpha = new Setting("Blue", this, 1, 0, 1, false);

    private final Setting advancedRenderLine = new Setting("AdvancedRenderLine", this, "Advanced Render");

    private final Setting startRed = new Setting("Start Red", this, 0, 0, 1, false);
    private final Setting startGreen = new Setting("Start Green", this, 0, 0, 1, false);
    private final Setting startBlue = new Setting("Start Blue", this, 0, 0, 1, false);
    private final Setting startAlpha = new Setting("Start Alpha", this, 0, 0, 1, false);

    private final Setting endRed = new Setting("End Red", this, 1, 0, 1, false);
    private final Setting endGreen = new Setting("End Green", this, 0, 0, 1, false);
    private final Setting endBlue = new Setting("End Blue", this, 0, 0, 1, false);
    private final Setting endAlpha = new Setting("End Alpha", this, 1, 0, 1, false);

    public static AutoRer instance;

    public final List<BlockPos> placedList = new ArrayList<>();
    private final TimerUtils placeTimer = new TimerUtils();
    private final TimerUtils breakTimer = new TimerUtils();
    private final TimerUtils calcTimer = new TimerUtils();
    private final TimerUtils renderTimer = new TimerUtils();
    private final TimerUtils predictTimer = new TimerUtils();
    private final TimerUtils manualTimer = new TimerUtils();
    private final TimerUtils synsTimer = new TimerUtils();
    private ScheduledExecutorService executor;
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
    public static EntityPlayer currentTarget;
    private Thread thread;
    private BlockPos placePos, renderPos;
    private Entity lastHitEntity = null;
    public boolean rotating;

    public AutoRer() {
        super("AutoRer", Category.COMBAT);

        instance = this;

        setmgr.rSetting(lagProtect);

        setmgr.rSetting(placeRange);
        setmgr.rSetting(placeWallRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(breakWallRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(logic);
        setmgr.rSetting(terrain);
        setmgr.rSetting(switch_);
        setmgr.rSetting(fastCalc);
        setmgr.rSetting(motionCrystal);
        setmgr.rSetting(motionCalc);
        setmgr.rSetting(swing);
        setmgr.rSetting(instant);
        setmgr.rSetting(instantCalc);
        setmgr.rSetting(instantRotate);
        setmgr.rSetting(inhibit);
        setmgr.rSetting(sound);
        setmgr.rSetting(syns);
        setmgr.rSetting(rotate);
        setmgr.rSetting(rotateMode);
//        setmgr.rSetting(ai);
        setmgr.rSetting(calcDistSort);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(place);
        setmgr.rSetting(secondCheck);
        setmgr.rSetting(thirdCheck);
        setmgr.rSetting(armorBreaker);
        setmgr.rSetting(multiPlace);
        setmgr.rSetting(firePlace);
        setmgr.rSetting(liquidPlace);

        setmgr.rSetting(breakLine);
        setmgr.rSetting(break_);
        setmgr.rSetting(breakPriority);
        setmgr.rSetting(friend_);
        setmgr.rSetting(clientSide);
        setmgr.rSetting(manualBreaker);
        setmgr.rSetting(removeAfterAttack);
        setmgr.rSetting(antiCevBreakerMode);

        setmgr.rSetting(delayLine);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(calcDelay);
        setmgr.rSetting(clearDelay);
        setmgr.rSetting(multiplication);

        setmgr.rSetting(dmgLine);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(maxFriendDMG);
        setmgr.rSetting(lethalMult);

        setmgr.rSetting(threadLine);
        setmgr.rSetting(threadMode);
        setmgr.rSetting(threadDelay);
        setmgr.rSetting(threadSyns);
        setmgr.rSetting(threadSynsValue);
        setmgr.rSetting(threadPacketRots);
        setmgr.rSetting(threadSoundPlayer);

        setmgr.rSetting(renderLine);
        setmgr.rSetting(render);
        setmgr.rSetting(text);
        setmgr.rSetting(infoMode);
        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(alpha);

        setmgr.rSetting(advancedRenderLine);
        setmgr.rSetting(startRed);
        setmgr.rSetting(startGreen);
        setmgr.rSetting(startBlue);
        setmgr.rSetting(startAlpha);
        setmgr.rSetting(endRed);
        setmgr.rSetting(endGreen);
        setmgr.rSetting(endBlue);
        setmgr.rSetting(endAlpha);
    }

    public void onEnable() {
        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        predictTimer.reset();
        manualTimer.reset();
        currentTarget = null;
        rotating = false;
        renderPos = null;

        if(!threadMode.getValString().equalsIgnoreCase("None")) processMultiThreading();

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(motion);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(motion);

        if(thread != null) shouldInterrupt.set(false);
        if(executor != null) executor.shutdown();
        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        predictTimer.reset();
        manualTimer.reset();
        currentTarget = null;
        rotating = false;
        renderPos = null;
    }

    private void processMultiThreading() {
        if(threadMode.getValString().equalsIgnoreCase("While")) handleWhile();
        else if(!threadMode.getValString().equalsIgnoreCase("None")) handlePool(false);
    }

    private ScheduledExecutorService getExecutor() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoRer.getInstance(this), 0L, this.threadDelay.getValLong(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void handleWhile() {
        if(thread == null || thread.isInterrupted() || thread.isAlive() || (synsTimer.passedMillis(threadSynsValue.getValLong()) &&threadSyns.getValBoolean())) {
            if(thread == null) thread = new Thread(RAutoRer.getInstance(this));
            else if(synsTimer.passedMillis(threadSynsValue.getValLong()) && !shouldInterrupt.get() && threadSyns.getValBoolean()) {
                shouldInterrupt.set(true);
                synsTimer.reset();
                return;
            }
            if(thread != null && (thread.isInterrupted() || !thread.isAlive())) thread = new Thread(RAutoRer.getInstance(this));
            if(thread != null && thread.getState().equals(Thread.State.NEW)) {
                try {thread.start();} catch (Exception ignored) {}
                synsTimer.reset();
            }
        }
    }

    private void handlePool(boolean justDoIt) {
        if(justDoIt || executor == null || executor.isTerminated() || executor.isShutdown() || (synsTimer.passedMillis(threadSynsValue.getValLong()) && threadSyns.getValBoolean())) {
            if(executor != null) executor.shutdown();
            executor = getExecutor();
            synsTimer.reset();
        }
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(renderTimer.passedMillis(clearDelay.getValLong())) {
            placedList.clear();
            renderTimer.reset();
            renderPos = null;
        }

        currentTarget = EntityUtil.getTarget(targetRange.getValFloat());

        if(currentTarget == null) return;
        else super.setDisplayInfo("[" + currentTarget.getName() + "]");
        if(threadMode.getValString().equalsIgnoreCase("None")) {
            if (manualBreaker.getValBoolean()) manualBreaker();
            if (motionCrystal.getValBoolean()) return;
            else if (motionCalc.getValBoolean() && fastCalc.getValBoolean()) return;
            if (fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
                doCalculatePlace();
                calcTimer.reset();
            }

            if (multiplication.getValInt() == 1) doAutoRerLogic(null, false);
            else for (int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(null, false);
        } else processMultiThreading();
    }

    public void doAutoRerForThread() {
        if(mc.player == null || mc.world == null) return;
        if(manualBreaker.getValBoolean()) manualBreaker();
        if(fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            doCalculatePlace();
            calcTimer.reset();
        }

        if(multiplication.getValInt() == 1) doAutoRerLogic(null, true);
        else for(int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(null, true);
    }

    private void manualBreaker() {
        RayTraceResult result = mc.objectMouseOver;
        if(manualTimer.passedMillis(200) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.BOW && mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && result != null) {
            if(result.typeOfHit.equals(RayTraceResult.Type.ENTITY) && result.entityHit instanceof EntityEnderCrystal) {
                mc.player.connection.sendPacket(new CPacketUseEntity(result.entityHit));
                manualTimer.reset();
            } else if(result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                for (Entity target : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY() + 1.0, mc.objectMouseOver.getBlockPos().getZ())))) {
                    if(!(target instanceof EntityEnderCrystal)) continue;
                    mc.player.connection.sendPacket(new CPacketUseEntity(target));
                    manualTimer.reset();
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> motion = new Listener<>(event -> {
        if(!motionCrystal.getValBoolean() || currentTarget == null) return;
        if(motionCalc.getValBoolean() && fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            doCalculatePlace();
            calcTimer.reset();
        }
        if(multiplication.getValInt() == 1) doAutoRerLogic(event, false);
        else for(int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(event, false);
    });

    private void doAutoRerLogic(EventPlayerMotionUpdate event, boolean thread) {
        if(logic.getValString().equalsIgnoreCase("PlaceBreak")) {
            doPlace(event, thread);
            doBreak();
        } else {
            doBreak();
            doPlace(event, thread);
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(renderPos != null){
            if(render.checkValString("Default")) RenderUtil.drawBlockESP(placePos, red.getValFloat(), green.getValFloat(), blue.getValFloat());
            else if (render.getValString().equalsIgnoreCase("Advanced")) RenderUtil.drawGradientFilledBox(placePos, new Color(startRed.getValFloat(), startGreen.getValFloat(), startBlue.getValFloat(), startAlpha.getValFloat()), new Color(endRed.getValFloat(), endGreen.getValFloat(), endBlue.getValFloat(), endAlpha.getValFloat()));
            if (text.getValBoolean()) {
                 float targetDamage = CrystalUtils.calculateDamage(mc.world, placePos.getX() + 0.5, placePos.getY() + 1, placePos.getZ() + 0.5, currentTarget, terrain.getValBoolean());
                 RenderUtil.drawText(placePos, ((Math.floor(targetDamage) == targetDamage) ? String.valueOf(Integer.valueOf((int) targetDamage)) : String.format("%.1f", targetDamage)));
            }
        }
    }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if(instantRotate.getValBoolean() && !motionCrystal.getValBoolean() && (rotate.getValString().equalsIgnoreCase("Break") || rotate.getValString().equalsIgnoreCase("All"))) {
            float[] rots = RotationUtils.getRotationToPos(pos);
            mc.player.rotationYaw = rots[0];
            mc.player.rotationPitch = rots[1];
        }
        CPacketUseEntity packet = new CPacketUseEntity();
        packet.entityId = entityID;
        packet.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(packet);
        breakTimer.reset();
        predictTimer.reset();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSpawnObject && instant.getValBoolean()) {
            SPacketSpawnObject packet =  (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                if(!(mc.world.getEntityByID(packet.getEntityID()) instanceof EntityEnderCrystal)) return;
                BlockPos toRemove = null;
                for (BlockPos pos : placedList) {
                    boolean canSee = EntityUtil.canSee(pos);
                    if (mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) >= (canSee ? breakRange.getValDouble() : breakWallRange.getValDouble())) break;

                    if(instantCalc.getValBoolean() && currentTarget != null) {
                        float targetDamage = CrystalUtils.calculateDamage(pos, currentTarget, terrain.getValBoolean());
                        if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                            float selfDamage = CrystalUtils.calculateDamage(pos, mc.player, terrain.getValBoolean());
                            if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                                toRemove = pos;
                                if (inhibit.getValBoolean()) try {lastHitEntity = mc.world.getEntityByID(packet.getEntityID());} catch (Exception ignored) {}
                                attackCrystalPredict(packet.getEntityID(), pos);
                                swing();
                            }
                        }
                    } else {
                        toRemove = pos;
                        if (inhibit.getValBoolean()) try {lastHitEntity = mc.world.getEntityByID(packet.getEntityID());} catch (Exception ignored) {}
                        attackCrystalPredict(packet.getEntityID(), pos);
                        swing();
                    }

                    break;
                }
                if (toRemove != null) placedList.remove(toRemove);
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && ((inhibit.getValBoolean() && lastHitEntity != null) || (sound.getValBoolean()))) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) if (lastHitEntity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) lastHitEntity.setDead();
            if(threadMode.checkValString(ThreadMode.Sound.name()) && isRightThread() && mc.player != null && mc.player.getDistanceSq(new BlockPos(packet.getX(), packet.getY(), packet.getZ())) < MathUtil.square(threadSoundPlayer.getValInt())) handlePool(true);
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) try {placedList.add(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos());} catch (Exception ignored) {}
        if(removeAfterAttack.getValBoolean() && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if(packet.getAction().equals(CPacketUseEntity.Action.ATTACK) && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).setDead();
                try {mc.world.removeEntityFromWorld(packet.entityId);} catch(Exception ignored) {}
            }
        }
    });

    private boolean isRightThread() {
        return mc.isCallingFromMinecraftThread() || (!this.threadOngoing.get());
    }

    private void doCalculatePlace() {
        try {calculatePlace();} catch (Exception e) {if(lagProtect.getValBoolean()) super.setToggled(false);}
    }

    private void calculatePlace() {
        double maxDamage = 0.5;
        BlockPos placePos = null;
        List<BlockPos> sphere = CrystalUtils.getSphere(placeRange.getValFloat(), true, false);

        if(calcDistSort.getValBoolean()) {
            Comparator<BlockPos> comparator = (first, second) -> {
                double firstDist = mc.player.getDistanceSq(first), secondDist = mc.player.getDistanceSq(second);
                return (int) (secondDist - firstDist);
            };

            sphere.sort(comparator);
        }

        for(int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);

            if(thirdCheck.getValBoolean() && !isPosValid(pos)) continue;
            if(CrystalUtils.canPlaceCrystal(pos, secondCheck.getValBoolean(), true, multiPlace.getValBoolean(), firePlace.getValBoolean(), liquidPlace.getValBoolean())) {
                float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean());

                if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                    float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean());

                    if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 < mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            placePos = pos;
                        }
                    }
                }
            }
        }
        this.placePos = placePos;
    }

    private boolean isPosValid(BlockPos pos) {
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= (EntityUtil.canSee(pos) ?  placeRange.getValDouble() : placeWallRange.getValDouble());
    }

    private void doPlace(EventPlayerMotionUpdate event, boolean thread) {
        if(!place.getValBoolean() || !placeTimer.passedMillis(placeDelay.getValLong()) || (placePos == null && fastCalc.getValBoolean())) return;

        if(!fastCalc.getValBoolean()) {
            doCalculatePlace();

            if(placePos == null) return;
        }

        if(syns.getValBoolean() && placedList.contains(placePos)) return;

        SilentSwitchBypass bypass = new SilentSwitchBypass(Items.END_CRYSTAL);
        EnumHand hand = null;
        boolean offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);
        boolean silentBypass = switch_.getValString().equals("SilentBypass");
        int oldSlot = mc.player.inventory.currentItem;
        int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);

        if(crystalSlot == -1 && !silentBypass && !offhand) return;

        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !offhand) {
            if(switch_.getValString().equals("None")) return;
            else if ("Normal".equals(switch_.getValString())) InventoryUtil.switchToSlot(crystalSlot, false);
            else if ("Silent".equals(switch_.getValString())) InventoryUtil.switchToSlot(crystalSlot, true);
            else if (silentBypass) bypass.doSwitch();
        }

        if(mc.player == null) return;
        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;
        if(mc.player.isHandActive()) hand = mc.player.getActiveHand();

        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};

        if(rotate.getValString().equalsIgnoreCase("Place") || rotate.getValString().equalsIgnoreCase("All") && currentTarget != null) {
            float[] rots = RotationUtils.getRotation(currentTarget);
            if(!thread) {
                if (!motionCrystal.getValBoolean()) {
                    mc.player.rotationYaw = rots[0];
                    mc.player.rotationPitch = rots[1];
                } else if (event != null) {
                    event.setYaw(rots[0]);
                    event.setPitch(rots[1]);
                }
            } else if(threadPacketRots.getValBoolean()) mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        }

        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + ( double ) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(( double ) placePos.getX() + 0.5, ( double ) placePos.getY() - 0.5, ( double ) placePos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        if(!swing.checkValString(SwingMode.None.name())) mc.player.connection.sendPacket(new CPacketAnimation(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        placeTimer.reset();

        renderPos = placePos;

        if(ai.getValBoolean()) {
            float targetDamage = CrystalUtils.calculateDamage(mc.world, placePos.getX() + 0.5, placePos.getY() + 1, placePos.getZ() + 0.5, currentTarget, terrain.getValBoolean());
            float selfDamage = CrystalUtils.calculateDamage(mc.world, placePos.getX() + 0.5, placePos.getY() + 1, placePos.getZ() + 0.5, mc.player, terrain.getValBoolean());
            AutoRerAI.collect(placePos, targetDamage, selfDamage);
        }

        if((rotate.getValString().equalsIgnoreCase("Place") || rotate.getValString().equalsIgnoreCase("All")) && rotateMode.getValString().equalsIgnoreCase("Silent")) {
            mc.player.rotationYaw = oldRots[0];
            mc.player.rotationPitch = oldRots[1];
        }
        if(hand != null) mc.player.setActiveHand(hand);
        if(oldSlot != -1 && !silentBypass) {
            if (switch_.getValString().equals(SwitchMode.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
        } else if(silentBypass) bypass.doSwitch();
    }

    private Entity getCrystalForAntiCevBreaker() {
        Entity crystal = null;
        String mode = antiCevBreakerMode.getValString();

        if(!mode.equals("None")) {
            if(mode.equals("Cev") || mode.equals("Both")) {
                for(Vec3i vec : AntiCevBreakerVectors.Cev.vectors) {
                    BlockPos pos = mc.player.getPosition().add(vec);
                    for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                        if(entity instanceof EntityEnderCrystal) {
                            crystal = entity;
                        }
                    }
                }
            }
            if(mode.equals("Civ") || mode.equals("Both")) {
                for(Vec3i vec : AntiCevBreakerVectors.Civ.vectors) {
                    BlockPos pos = mc.player.getPosition().add(vec);
                    for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                        if(entity instanceof EntityEnderCrystal) {
                            crystal = entity;
                        }
                    }
                }
            }
        }

        return crystal;
    }

    private Entity getCrystalWithMaxDamage() {
        Entity crystal = null;
        double maxDamage = 0.5;

        for(int i = 0; i < mc.world.loadedEntityList.size(); ++i) {
            Entity entity = mc.world.loadedEntityList.get(i);

            if(entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? breakRange.getValDouble() : breakWallRange.getValDouble())) {
                Friend friend = getNearFriendWithMaxDamage(entity);
                double targetDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());

                if(friend != null && !friend_.getValString().equalsIgnoreCase(FriendMode.None.name())) {
                    if(friend_.getValString().equalsIgnoreCase(FriendMode.AntiTotemPop.name()) && friend.isTotemPopped) return null;
                    else if(friend.isTotemFailed) return null;
                    if(friend.damage >= maxFriendDMG.getValInt()) return null;
                }

                if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                    double selfDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, mc.player, terrain.getValBoolean());

                    if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            crystal = entity;
                        }
                    }
                }
            }
        }

        return crystal;
    }


    private void doBreak() {
        if(!break_.getValBoolean() || !breakTimer.passedMillis(breakDelay.getValLong())) return;

        Entity crystal, crystalWithMaxDamage = getCrystalWithMaxDamage();
        
        if(breakPriority.getValString().equals("Damage")) crystal = crystalWithMaxDamage;
        else crystal = getCrystalForAntiCevBreaker();

        if(crystal == null) crystal = crystalWithMaxDamage;
        if(crystal == null) return;

        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};

        if(rotate.getValString().equalsIgnoreCase("Break") || rotate.getValString().equalsIgnoreCase("All")) {
            float[] rots = RotationUtils.getRotation(crystal);
            mc.player.rotationYaw = rots[0];
            mc.player.rotationPitch = rots[1];
        }

        lastHitEntity = crystal;
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        swing();
        try {if(clientSide.getValBoolean()) mc.world.removeEntityFromWorld(crystal.entityId);} catch (Exception ignored) {}
        breakTimer.reset();

        if((rotate.getValString().equalsIgnoreCase("Break") || rotate.getValString().equalsIgnoreCase("All")) && rotateMode.getValString().equalsIgnoreCase("Silent")) {
            mc.player.rotationYaw = oldRots[0];
            mc.player.rotationPitch = oldRots[1];
        }

        BlockPos toRemove = null;

        if(syns.getValBoolean()) for(BlockPos pos : placedList) if(crystal.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= 3) toRemove = pos;
        if(toRemove != null) placedList.remove(toRemove);
    }

    private void swing() {
        if(swing.checkValString(SwingMode.None.name())) return;
        if(swing.getValString().equals(SwingMode.PacketSwing.name())) mc.player.connection.sendPacket(new CPacketAnimation(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        else mc.player.swingArm(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
    }

    private Friend getNearFriendWithMaxDamage(Entity entity) {
        ArrayList<Friend> friendsWithMaxDamage = new ArrayList<>();

        for(EntityPlayer player : mc.world.playerEntities) {
            if(mc.player == player) continue;
            if(FriendManager.instance.isFriend(player)) {
                double friendDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());
                if(friendDamage <= maxFriendDMG.getValInt() || friendDamage * lethalMult.getValDouble() >= player.getHealth() + player.getAbsorptionAmount()) friendsWithMaxDamage.add(new Friend(player, friendDamage, friendDamage * lethalMult.getValDouble() >= player.getHealth() + player.getAbsorptionAmount()));
            }
        }

        Friend nearFriendWithMaxDamage = null;
        double maxDamage = 0.5;

        for(Friend friend : friendsWithMaxDamage) {
            double friendDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());
            if(friendDamage > maxDamage) {
                maxDamage = friendDamage;
                nearFriendWithMaxDamage = new Friend(friend.friend, friendDamage);
            }
        }

        return nearFriendWithMaxDamage;
    }

    public enum ThreadMode {None, Pool, Sound, While}
    public enum Render {None, Default, Advanced}
    public enum InfoMode {Target, Damage, Both}
    public enum Rotate {Off, Place, Break, All}
    public enum Raytrace {None, Place, Break, Both}
    public enum SwitchMode {None, Normal, Silent, SilentBypass}
    public enum SwingMode {MainHand, OffHand, PacketSwing, None}
    public enum FriendMode {None, AntiTotemFail, AntiTotemPop}
    public enum LogicMode {PlaceBreak, BreakPlace}
    public enum RotateMode {Normal, Silent}
    public enum AntiCevBreakerMode {None, Cev, Civ, Both}
    public enum BreakPriority {Damage, CevBreaker}

    public enum AntiCevBreakerVectors {
        Cev(Arrays.asList(new Vec3i(0, 2, 0))),
        Civ(Arrays.asList(new Vec3i(1, 2, 0), new Vec3i(-1, 2, 0), new Vec3i(0, 2, 1), new Vec3i(0, 2, -1), new Vec3i(1, 2, 1), new Vec3i(-1, 2, -1), new Vec3i(1, 2, -1), new Vec3i(-1, 2, 1)));

        public final List<Vec3i> vectors;

        AntiCevBreakerVectors(List<Vec3i> vectors) {
            this.vectors = vectors;
        }
    }

    private static class Friend {
        public final EntityPlayer friend;
        public double damage;
        public boolean isTotemPopped;
        public boolean isTotemFailed = false;

        public Friend(EntityPlayer friend, double damage) {
            this.friend = friend;
            this.damage = damage;
            this.isTotemPopped = false;
        }

        public Friend(EntityPlayer friend, double damage, boolean isTotemPopped) {
            this.friend = friend;
            this.damage = damage;
            if(isTotemPopped) isTotemFailed = !(mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING));
            this.isTotemPopped = isTotemPopped;
        }
    }

    public static class RAutoRer implements Runnable {
        private static RAutoRer instance;
        private AutoRer autoRer;

        public static RAutoRer getInstance(AutoRer autoRer) {
            if(instance == null) {
                instance = new RAutoRer();
                instance.autoRer = autoRer;
            }
            return instance;
        }

        @Override
        public void run() {
            if(autoRer.threadMode.getValString().equalsIgnoreCase("While")) {
                while (autoRer.isToggled() && autoRer.threadMode.getValString().equalsIgnoreCase("While")) {
                    if(autoRer.shouldInterrupt.get()) {
                        autoRer.shouldInterrupt.set(false);
                        autoRer.synsTimer.reset();
                        autoRer.thread.interrupt();
                    }
                    autoRer.threadOngoing.set(true);
                    autoRer.doAutoRerForThread();
                    autoRer.threadOngoing.set(false);
                    try {Thread.sleep(autoRer.threadDelay.getValLong());} catch (InterruptedException e) {autoRer.thread.interrupt();}
                }
            } else if(!autoRer.threadMode.getValString().equalsIgnoreCase("None")) {
                autoRer.threadOngoing.set(true);
                autoRer.doAutoRerForThread();
                autoRer.threadOngoing.set(false);
            }
        }
    }
}