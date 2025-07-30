package wtf.silhouette.shaeware.features.modules.render;

import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.BooleanSetting;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.EnumSetting;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.interfaces.IEnumSetting;
import wtf.silhouette.shaeware.mixins.accessors.WorldRendererAccessor;
import wtf.silhouette.shaeware.utils.RenderUtil;

import java.util.function.BooleanSupplier;

public final class ESPModule extends AbstractModule {

    private final EnumSetting<SettingDisplayMode> displayMode = new EnumSetting<>("Mode", SettingDisplayMode.PLAYER);

    private final BooleanSetting playerMaster = new BooleanSetting("Player", true);
    private final BooleanSetting nameTagMaster = new BooleanSetting("NameTag", true);
    private final BooleanSetting healthNameTag = new BooleanSetting("Health", true);
    private final BooleanSetting distanceNameTag = new BooleanSetting("Distance", true);

    private final BooleanSetting chestMaster = new BooleanSetting("Chest", true);

    private final BooleanSetting blockMaster = new BooleanSetting("Block", true);

    private final BooleanSetting itemMaster = new BooleanSetting("Item", true);

    public ESPModule() {
        super("ESP", "Extrasensory perception", ModuleCategory.RENDER);

        final BooleanSupplier isPlayerMode = () -> displayMode.getValue() == SettingDisplayMode.PLAYER;
        final BooleanSupplier isChestMode = () -> displayMode.getValue() == SettingDisplayMode.CHEST;
        final BooleanSupplier isBlockMode = () -> displayMode.getValue() == SettingDisplayMode.BLOCK;
        final BooleanSupplier isItemMode = () -> displayMode.getValue() == SettingDisplayMode.ITEM;

        playerMaster.supplyIf(isPlayerMode);
        nameTagMaster.supplyIf(isPlayerMode);
        healthNameTag.supplyIf(isPlayerMode);
        distanceNameTag.supplyIf(isPlayerMode);

        chestMaster.supplyIf(isChestMode);

        blockMaster.supplyIf(isBlockMode);

        itemMaster.supplyIf(isItemMode);
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> screenEventListener = event -> {
        final DrawContext context = event.context();
        final TextRenderer textRenderer = mc.textRenderer;

        int[] viewport = new int[4];

        GL32.glGetIntegerv(GL32.GL_VIEWPORT, viewport);

        MatrixStack matrixStack = RenderUtil.createMatrixStack(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));

        if (playerMaster.getValue()) {
            this.renderPlayerESP(viewport, matrixStack.peek().getPositionMatrix(), context, textRenderer);
        }
    };

    private void renderPlayerESP(final int[] viewport, final Matrix4f matrix, final DrawContext context, final TextRenderer textRenderer) {
        for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            if (entity == null || !entity.isAlive() || entity == MinecraftClient.getInstance().player || !(entity instanceof PlayerEntity)) {
                continue;
            }

            float[] projected = RenderUtil.projectEntity(viewport, matrix, entity);
            if (projected == null) {
                continue;
            }


            if (!((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getFrustum().isVisible(entity.getBoundingBox())) {
                continue;
            }

            float inv = (float) (1.0f / MinecraftClient.getInstance().getWindow().getScaleFactor());

            projected[0] *= inv;
            projected[1] *= inv;
            projected[2] *= inv;
            projected[3] *= inv;

            projected[2] -= projected[0];
            projected[3] -= projected[1];

            final float x = projected[0];
            final float y = projected[1];
            final float width = projected[2];
            final float height = projected[3];

            String nameTagDisplay = entity.getName().getString();

            final boolean shouldDisplayHealth = healthNameTag.getValue();
            final boolean shouldDisplayDistance = distanceNameTag.getValue();

            if (shouldDisplayHealth || shouldDisplayDistance) {
                nameTagDisplay += " §7[";

                if (shouldDisplayHealth) {
                    float health = ((PlayerEntity) entity).getHealth();
                    String healthColor = health > 15 ? "§a" : health > 7 ? "§e" : "§c";
                    nameTagDisplay += healthColor + "HP:§f " + String.format("%.1f", health);
                }

                if (shouldDisplayHealth && shouldDisplayDistance) {
                    nameTagDisplay += " §8| ";
                }

                if (shouldDisplayDistance) {
                    float distance = mc.player.distanceTo(entity);
                    String distanceColor = distance > 20 ? "§a" : distance > 10 ? "§e" : "§c";
                    nameTagDisplay += distanceColor + "Dist:§f " + String.format("%.1f", distance) + "m";
                }

                nameTagDisplay += "§7]";
            }


            if (nameTagMaster.getValue()) {
                final float nameTagWidth = textRenderer.getWidth(nameTagDisplay) / 2.0f;
                final float nameTagHeight = textRenderer.fontHeight / 2.0f;

                final int backGroundX = (int) ((x + width / 2) - ((float) textRenderer.getWidth(nameTagDisplay) / 2) - 2);
                final int backGroundY = (int) ((y - 20) - 2);
                final int backGroundWidth = (int) ((float) textRenderer.getWidth(nameTagDisplay)) + 4;
                final int backGroundHeight = textRenderer.fontHeight + 4;

                context.fill(backGroundX, backGroundY, backGroundX + backGroundWidth, backGroundY + backGroundHeight, 0x80000000);
                context.drawText(textRenderer, nameTagDisplay, (int) (x + width / 2 - nameTagWidth), (int) (y - 15 - nameTagHeight), 0xFFFFFFFF, true);
            }
        }
    }

    public boolean getNameTagMaster() {
        return nameTagMaster.getValue() && playerMaster.getValue();
    }

    @AllArgsConstructor
    private enum SettingDisplayMode implements IEnumSetting {
        PLAYER("Player"), CHEST("Chest"), BLOCK("Block"), ITEM("Item");

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }
}
