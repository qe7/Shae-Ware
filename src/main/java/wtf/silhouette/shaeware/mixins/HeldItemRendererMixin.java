package wtf.silhouette.shaeware.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.silhouette.shaeware.features.modules.combat.killaura.KillAuraModule;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow
    private float prevEquipProgressMainHand;

    @Shadow
    private float prevEquipProgressOffHand;

    @Shadow
    private float equipProgressOffHand;

    @Shadow
    private float equipProgressMainHand;

    @Shadow
    private ItemStack mainHand;

    @Shadow
    private ItemStack offHand;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow
    protected abstract void swingArm(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm);

    @Shadow
    protected abstract void applyBrushTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, float equipProgress);

    @Shadow
    protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Shadow
    protected abstract void renderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress);

    @Shadow
    protected abstract void renderMapInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack);

    @Shadow
    protected abstract void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player);

    @Shadow
    public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    /**
     * @author Shae
     * @reason Animations
     */
    @Overwrite
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!player.isUsingSpyglass()) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(1.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0f));

            if (item.isEmpty()) {
                if (bl && !player.isInvisible()) {
                    this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
                }
            } else if (item.contains(DataComponentTypes.MAP_ID)) {
                if (bl && this.offHand.isEmpty()) {
                    this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
                } else {
                    this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
                }
            } else if (item.isOf(Items.CROSSBOW)) {
                boolean bl2 = CrossbowItem.isCharged(item);
                boolean bl3 = arm == Arm.RIGHT;
                int i = bl3 ? 1 : -1;
                if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    matrices.translate((float) i * -0.4785682F, -0.094387F, 0.05731531F);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * 65.3F));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) i * -9.785F));
                    float f = (float) item.getMaxUseTime(player) - ((float) player.getItemUseTimeLeft() - tickDelta + 1.0F);
                    float g = f / (float) CrossbowItem.getPullTime(item, player);
                    if (g > 1.0F) {
                        g = 1.0F;
                    }

                    if (g > 0.1F) {
                        float h = MathHelper.sin((f - 0.1F) * 1.3F);
                        float j = g - 0.1F;
                        float k = h * j;
                        matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
                    }

                    matrices.translate(g * 0.0F, g * 0.0F, g * 0.04F);
                    matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
                    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) i * 45.0F));
                } else {
                    this.swingArm(swingProgress, equipProgress, matrices, i, arm);
                    if (bl2 && swingProgress < 0.001F && bl) {
                        matrices.translate((float) i * -0.641864F, 0.0F, 0.0F);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * 10.0F));
                    }
                }

                this.renderItem(player, item, bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
            } else {
                boolean bl2 = arm == Arm.RIGHT;
                int l = bl2 ? 1 : -1;
                if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand || KillAuraModule.isBlocking()) {
                    switch (item.getUseAction()) {
                        case NONE:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        case EAT:
                        case DRINK:
                            this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item, player);
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        case BLOCK:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            if (!(item.getItem() instanceof ShieldItem)) {
                                matrices.translate(arm == Arm.RIGHT ? -0.2f : 0.2f, 0.2f, 0.0f);
                                int armSide = (arm == Arm.RIGHT) ? 1 : -1;
                                float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(armSide * (45.0f + f * -20.0f)));
                                float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
                                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(armSide * g * -20.0f));
                                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * -80.0f));
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(armSide * -30.0f));

                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30));
                                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-70));
                                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(25));
                            }
                            break;
                        case BOW:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.translate((float) l * -0.2785682F, 0.18344387F, 0.15731531F);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) l * 35.3F));
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) l * -9.785F));
                            float something = (float) item.getMaxUseTime(player) - ((float) player.getItemUseTimeLeft() - tickDelta + 1.0F);
                            float something2 = something / 20.0F;
                            something2 = (something2 * something2 + something2 * 2.0F) / 3.0F;
                            if (something2 > 1.0F) {
                                something2 = 1.0F;
                            }

                            if (something2 > 0.1F) {
                                float g = MathHelper.sin((something - 0.1F) * 1.3F);
                                float h = something2 - 0.1F;
                                float j = g * h;
                                matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
                            }

                            matrices.translate(something2 * 0.0F, something2 * 0.0F, something2 * 0.04F);
                            matrices.scale(1.0F, 1.0F, 1.0F + something2 * 0.2F);
                            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) l * 45.0F));
                            break;
                        case SPEAR:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.translate((float) l * -0.5F, 0.7F, 0.1F);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) l * 35.3F));
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) l * -9.785F));
                            float m = (float) item.getMaxUseTime(player) - ((float) player.getItemUseTimeLeft() - tickDelta + 1.0F);
                            float f = m / 10.0F;
                            if (f > 1.0F) {
                                f = 1.0F;
                            }

                            if (f > 0.1F) {
                                float g = MathHelper.sin((m - 0.1F) * 1.3F);
                                float h = f - 0.1F;
                                float j = g * h;
                                matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
                            }

                            matrices.translate(0.0F, 0.0F, f * 0.2F);
                            matrices.scale(1.0F, 1.0F, 1.0F + f * 0.2F);
                            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) l * 45.0F));
                            break;
                        case BRUSH:
                            this.applyBrushTransformation(matrices, tickDelta, arm, item, player, equipProgress);
                            break;
                        case BUNDLE:
                            this.swingArm(swingProgress, equipProgress, matrices, l, arm);
                    }
                } else if (player.isUsingRiptide()) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    matrices.translate((float) l * -0.4F, 0.8F, 0.3F);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) l * 65.0F));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) l * -85.0F));
                } else {
                    this.swingArm(swingProgress, equipProgress, matrices, l, arm);
                }

                this.renderItem(player, item, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
            }

            matrices.pop();
        }
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getItemUseTimeLeft()I",
            ordinal = 2
    ))
    private int hookItemUseItem(AbstractClientPlayerEntity instance) {
        var item = instance.getMainHandStack().getItem();

        if (item instanceof SwordItem && MinecraftClient.getInstance().player.isUsingItem() || KillAuraModule.isBlocking()) {
            return 7200;
        }

        return instance.getItemUseTimeLeft();
    }

    @ModifyArg(
            method = "updateHeldItems",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 2),
            index = 0
    )
    private float modifyMainHandEquipProgress(float value, @Local(ordinal = 0) ItemStack itemStack) {
        if (this.mainHand == itemStack) {
            return 1.0F - this.equipProgressMainHand;
        }

        return value;
    }

    @ModifyArg(method = "renderFirstPersonItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V",
            ordinal = 3
    ), index = 2)
    private float applyEquipOffset(float equipProgress) {
        if (KillAuraModule.isBlocking()) {
            return 0.0f;
        }

        return equipProgress;
    }
}
