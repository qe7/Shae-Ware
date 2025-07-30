package wtf.silhouette.shaeware.features.modules.combat.killaura;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import wtf.silhouette.shaeware.components.RotationComponent;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.game.PacketEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.TickEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.component.ComponentFactory;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.mode.NullMode;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.*;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.interfaces.IEnumSetting;
import wtf.silhouette.shaeware.mixins.accessors.ClientPlayerInteractionManagerAccessor;
import wtf.silhouette.shaeware.utils.MathUtil;
import wtf.silhouette.shaeware.utils.Stopwatch;
import wtf.silhouette.shaeware.utils.player.RotationUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class KillAuraModule extends AbstractModule {

    private final EnumSetting<TargetMode> targetMode = new EnumSetting<>("Target", TargetMode.SINGLE);

    private final EnumSetting<SortMode> sortMode = new EnumSetting<>("Sort", SortMode.HEALTH);

    private final EnumSetting<DelayMode> delayMode = new EnumSetting<>("Delay", DelayMode.ONE_POINT_EIGHT);

    private final ModeSetting autoBlockMode = new ModeSetting("AutoBlock", "None", new NullMode(this, "None"), new Test(this, "Test"));

    // Only used in multi target mode. Will send a packet rotation to the server when attacking an entity.
    private final BooleanSetting packetRotation = new BooleanSetting("Multi Packet Rotation", true);

    private final BooleanSetting requireMouseDown = new BooleanSetting("Require Mouse Down", false);

    private final BooleanSetting screenCheck = new BooleanSetting("Screen Check", true);

    private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", true);

    private final IntSetting minCPS = new IntSetting("Min CPS", 8, 1, 20, 1);
    private final IntSetting maxCPS = new IntSetting("Max CPS", 12, 1, 20, 1);

    private final IntSetting maxEntities = new IntSetting("Max Entities", 3, 1, 20, 1);

    private final IntSetting lookFor = new IntSetting("Look For", 1, 0, 3, 1);

    private final IntSetting switchDelay = new IntSetting("Switch Delay", 250, 0, 1000, 50);

    private final FloatSetting blockRange = new FloatSetting("Block Range", 4.0f, 1.0f, 6.0f, 0.1f);
    private final FloatSetting attackRange = new FloatSetting("Attack Range", 4.0f, 1.0f, 6.0f, 0.1f);

    private final Stopwatch attackStopwatch = new Stopwatch();
    private final Stopwatch switchStopwatch = new Stopwatch();

    private int ticksSinceLooking = 0;
    private int ticksSinceUnblock = 0;
    private int switchIndex = 0;

    private PlayerEntity targetEntity = null;
    private List<PlayerEntity> targetList = new ArrayList<>();

    @Getter
    private static boolean blocking = false;
    private boolean canAttack = false;

    public KillAuraModule() {
        super("Kill Aura", "Automatically hits entities.", ModuleCategory.COMBAT);

        final BooleanSupplier isOnePointEight = () -> delayMode.getValue() == DelayMode.ONE_POINT_EIGHT;
        final BooleanSupplier isSwitchMode = () -> targetMode.getValue() == TargetMode.SWITCH;
        final BooleanSupplier isMultiMode = () -> targetMode.getValue() == TargetMode.MULTI;

        packetRotation.supplyIf(isMultiMode);

        requireMouseDown.supplyIf(isOnePointEight);

        minCPS.supplyIf(isOnePointEight);
        maxCPS.supplyIf(isOnePointEight);

        switchDelay.supplyIf(isSwitchMode);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        targetEntity = null;
        targetList.clear();

        blocking = false;

        ticksSinceLooking = 0;
        ticksSinceUnblock = 0;
        switchIndex = 0;

        attackStopwatch.reset();
        switchStopwatch.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        targetEntity = null;
        targetList.clear();

        blocking = false;
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> screenEventListener = event -> {
        this.setSuffix(targetMode.getValue().getName());
    };

    @SubscribeEvent
    private final Listener<TickEvent> tickEventListener = event -> {
        if (event.isPost()) return;

        if (mc.player == null || mc.world == null) return;

        if (mc.currentScreen != null && screenCheck.getValue()) return;

        boolean isMouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (requireMouseDown.getValue() && !isMouseDown) {
            if (blocking) {
                blocking = false;
                ticksSinceUnblock = 0;
                ticksSinceLooking = 0;
                unblock();
            }
            return;
        }

        final List<PlayerEntity> entities = new ArrayList<>(mc.world.getPlayers());

        entities.removeIf(entity -> entity == null || entity == mc.player || !entity.isAlive() || entity.getHealth() <= 0.0f || entity.isSpectator() || entity.isCreative() || entity.getUuid().equals(mc.player.getUuid()));

        if (entities.isEmpty()) {
            targetEntity = null;
            targetList.clear();
            ticksSinceLooking = 0;
            unblock();
            return;
        }

        final float searchRange = Math.max(attackRange.getValue(), blockRange.getValue());

        entities.removeIf(entity -> entity.squaredDistanceTo(mc.player) > searchRange * searchRange);

        if (throughWalls.getValue()) {
            entities.removeIf(entity -> !mc.player.canSee(entity));
        } else {
            entities.removeIf(entity -> !mc.player.canSee(entity));
        }

        final Comparator<PlayerEntity> comparator = switch (sortMode.getValue()) {
            case HEALTH -> Comparator.comparingDouble(PlayerEntity::getHealth).reversed();
            case HURT_TIME -> Comparator.<PlayerEntity>comparingInt(entity -> entity.hurtTime).reversed();
            case DISTANCE -> Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player));
        };

        entities.sort(comparator);

        targetList = new ArrayList<>(entities.subList(0, Math.min(maxEntities.getValue(), entities.size())));

        List<PlayerEntity> inAttackRange = new ArrayList<>(targetList.stream()
                .filter(entity -> mc.player.canInteractWithEntity(entity, attackRange.getValue() - 3 - 0.01))
                .toList());

        if (inAttackRange.isEmpty()) {
            targetEntity = null;
            switchIndex = 0;
            ticksSinceLooking = 0;
            unblock();
            return;
        }

        if (!mc.player.canInteractWithEntity(inAttackRange.getFirst(), searchRange - 3 - 0.01)) {
            inAttackRange.sort(Comparator.comparingDouble(e -> mc.player.distanceTo(e)));
        }

        // TargetMode logic
        switch (targetMode.getValue()) {
            case SINGLE -> targetEntity = inAttackRange.getFirst();
            case SWITCH -> {
                if (inAttackRange.size() >= 2) {
                    if (switchStopwatch.hasTimeElapsed(switchDelay.getValue(), TimeUnit.MILLISECONDS, true)) {
                        switchIndex = (switchIndex + 1) % inAttackRange.size();
                        targetEntity = inAttackRange.get(switchIndex);
                    }
                } else {
                    targetEntity = inAttackRange.getFirst();
                }
            }
        }

        if (targetEntity == null || targetEntity == mc.player || !inAttackRange.contains(targetEntity) || targetEntity.isDead()) {
            targetEntity = null;
            switchIndex = 0;
            unblock();
            return;
        }

        float[] rotations = RotationUtil.getEntityRotations(targetEntity);

        if (rotations == null) {
            return;
        }

        ComponentFactory.get(RotationComponent.class).setNextRotations(RotationUtil.applyGCDPatch(rotations));

        if (ticksSinceLooking < lookFor.getValue()) {
            ticksSinceLooking++;
            return;
        }

        boolean delayMet = false;

        switch (delayMode.getValue()) {
            case ONE_POINT_EIGHT -> {
                if (attackStopwatch.hasTimeElapsed(getNextDelay(), TimeUnit.MILLISECONDS, true)) {
                    delayMet = true;
                }
            }
            case ONE_POINT_NINE -> {
                if (mc.player.getAttackCooldownProgress(0.0f) >= 1.0f) {
                    delayMet = true;
                }
            }
        }

        if (delayMet) {
            if (!(autoBlockMode.getValue() instanceof NullMode)) {
                unblock();
            }
            mc.interactionManager.attackEntity(mc.player, targetEntity);
            mc.player.swingHand(mc.player.getActiveHand());
            if (!(autoBlockMode.getValue() instanceof NullMode)) {
                block();
            }
        }

        if (!mc.player.canInteractWithEntity(targetEntity, searchRange - 3)) if (canAttack || blocking) {
            unblock();
            canAttack = false;
        }

        if (mc.player.getMainHandStack().getItem() instanceof SwordItem && MinecraftClient.getInstance().player.isUsingItem() && !canAttack)
            unblock();
    };

    @SubscribeEvent
    private final Listener<PacketEvent> packetEventListener = event -> {
        if (event.isOutgoing() && event.getPacket() instanceof PlayerActionC2SPacket action && ticksSinceUnblock < 3 && canAttack) {
            if (action.getAction().equals(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM)) {
                event.setCancelled(true);
            }
        }
    };

    private long getNextDelay() {
        int cps = MathUtil.nextInt(minCPS.getValue(), maxCPS.getValue() + 1);
        long baseDelay = 1000L / cps;
        return baseDelay + MathUtil.nextInt(-3, 4);
    }

    private void block() {
        if (mc.player.getMainHandStack().getItem() instanceof SwordItem && targetEntity != null) {
            Item heldItem = mc.player.getMainHandStack().getItem();
            if (!blocking && heldItem instanceof SwordItem) {
                mc.interactionManager.interactEntity(mc.player, targetEntity, Hand.MAIN_HAND);
                ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).invokeSendSequencedPacket(mc.world, sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, ComponentFactory.get(RotationComponent.class).getCurrentRotation()[0], ComponentFactory.get(RotationComponent.class).getCurrentRotation()[1]));

                blocking = true;
            }
        }
    }

    private void unblock() {
        Item heldItem = mc.player.getMainHandStack().getItem();
        if (blocking && heldItem instanceof SwordItem) {
            final int selectedSlot = mc.player.getInventory().selectedSlot;
            final int nextSlot = (selectedSlot + 1) % 9;

            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(nextSlot));
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
            blocking = false;
            ticksSinceUnblock = 0;
        }
    }

    @AllArgsConstructor
    private enum TargetMode implements IEnumSetting {
        SINGLE("Single"), // Single target mode, targets the first entity in the list.
        SWITCH("Switch"), // Switch target mode, switches to the next entity in the list after a given delay.
        MULTI("Multi"); // Multi target mode, attacks multiple targets at once.

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    @AllArgsConstructor
    private enum SortMode implements IEnumSetting {
        HEALTH("Health"),
        HURT_TIME("Hurt Time"),
        DISTANCE("Distance");

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    @AllArgsConstructor
    private enum DelayMode implements IEnumSetting {
        ONE_POINT_EIGHT("1.8"), // Hit delay based off of CPS.
        ONE_POINT_NINE("1.9"); // Hit delay based off of the 1.9+ attack cooldown system.

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }
}
