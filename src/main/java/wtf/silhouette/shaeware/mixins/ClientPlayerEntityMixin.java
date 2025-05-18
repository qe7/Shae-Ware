package wtf.silhouette.shaeware.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.silhouette.shaeware.events.impl.player.MotionEvent;
import wtf.silhouette.shaeware.events.impl.player.SlowdownEvent;
import wtf.silhouette.shaeware.events.impl.player.TickEvent;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;

@Mixin(ClientPlayerEntity.class)
public final class ClientPlayerEntityMixin {

  @Unique
  private MotionEvent motionEvent;

  @Inject(method = "tick", at = @At("HEAD"))
  private void onTickPre(CallbackInfo ci) {
    ManagerFactory.get(EventManager.class).publishEvent(new TickEvent(TickEvent.Type.PRE));
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void onTickPost(CallbackInfo ci) {
    ManagerFactory.get(EventManager.class).publishEvent(new TickEvent(TickEvent.Type.POST));
  }

  @Inject(method = "sendMovementPackets", at = @At("HEAD"))
  private void onSendMovementPacketsPre(CallbackInfo ci) {
    ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
    this.motionEvent = new MotionEvent(MotionEvent.Type.PRE, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround(), player.horizontalCollision);
    ManagerFactory.get(EventManager.class).publishEvent(motionEvent);
  }

  @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"))
  private double modifyX(double original) {
    return motionEvent.getX();
  }

  @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"))
  private double modifyY(double original) {
    return motionEvent.getY();
  }

  @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"))
  private double modifyZ(double original) {
    return motionEvent.getZ();
  }

  @ModifyExpressionValue(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"))
  private boolean modifyOnGround(boolean original) {
    return motionEvent.isOnGround();
  }

  @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
  private float modifyYaw(float original) {
    return motionEvent.getYaw();
  }

  @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
  private float modifyPitch(float original) {
    return motionEvent.getPitch();
  }

  @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
  private boolean onItemSlowdown(ClientPlayerEntity player) {
    SlowdownEvent event = new SlowdownEvent(player.isUsingItem());
    ManagerFactory.get(EventManager.class).publishEvent(event);
    return !event.isCancelled() && player.isUsingItem();
  }
}
