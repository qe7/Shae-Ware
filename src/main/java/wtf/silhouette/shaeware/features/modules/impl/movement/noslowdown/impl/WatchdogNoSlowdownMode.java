package wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown.impl;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.game.PacketEvent;
import wtf.silhouette.shaeware.events.impl.player.MotionEvent;
import wtf.silhouette.shaeware.events.impl.player.SlowdownEvent;
import wtf.silhouette.shaeware.features.modules.api.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.impl.movement.noslowdown.NoSlowdownModule;

import java.util.Arrays;
import java.util.List;

public final class WatchdogNoSlowdownMode extends AbstractModuleMode<NoSlowdownModule> {

  private boolean wasOffGround, isCancelled, hasJumped;

  private final List<Item> consumables = Arrays.asList(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.POTION, Items.BOW, Items.MUSHROOM_STEW);

  public WatchdogNoSlowdownMode(NoSlowdownModule module, String name) {
    super(module, name);
  }

  @SubscribeEvent
  private final Listener<SlowdownEvent> onSlowdown = event -> {
    if (mc.player.isUsingItem()) {
      event.setCancelled(true);
      mc.player.setSprinting(true);
      isCancelled = true;
    } else {
      isCancelled = false;
      wasOffGround = false;
    }
  };

  @SubscribeEvent
  private final Listener<MotionEvent> onMotion = event -> {
    if (mc.player == null) {
      return;
    }

    Item heldItem = mc.player.getMainHandStack().getItem();
    if (heldItem instanceof SwordItem && !isConsumable(heldItem)) return;

    if (isCancelled) {
      if (!mc.player.isOnGround()) {
        wasOffGround = true;
      }

      if (wasOffGround) {
        if (mc.player.getVelocity().y <= 0) event.setY(event.getY() + 0.001093);
        else event.setOnGround(true);
      }
    } else {
      hasJumped = false;
    }
  };

  @SubscribeEvent
  private final Listener<PacketEvent> onPacket = event -> {
    if (event.getPacket() instanceof PlayerActionC2SPacket c07) {
      if (c07.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) hasJumped = false;
    }
  };

  private boolean isConsumable(Item item) {
    return consumables.contains(item);
  }
}
