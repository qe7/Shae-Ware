package wtf.silhouette.shaeware.features.modules.combat;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.BooleanSetting;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.IntSetting;
import wtf.silhouette.shaeware.mixins.accessors.BoundKeyAccessor;
import wtf.silhouette.shaeware.utils.MathUtil;
import wtf.silhouette.shaeware.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

public final class AutoClickerModule extends AbstractModule {

    private final BooleanSetting returnOnBlock = new BooleanSetting("Return on Block", true);

    private final IntSetting minCPS = new IntSetting("Min", 10, 1, 20, 1);
    private final IntSetting maxCPS = new IntSetting("Max", 14, 1, 20, 1);

    private final Stopwatch stopwatch = new Stopwatch();

    private long currentDelay = 0;

    public AutoClickerModule() {
        super("Auto Clicker", "Automatically clicks for you.", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        stopwatch.reset();
        currentDelay = getNextDelay();
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> screenEventListener = event -> {
        this.setSuffix(minCPS.getValue() + " - " + maxCPS.getValue());
    };

    @SubscribeEvent
    private final Listener<ScreenEvent> onTick = event -> {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;
        boolean isMouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        HitResult hitResult = mc.crosshairTarget;
        if (returnOnBlock.getValue() && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            if (isMouseDown) mc.options.attackKey.setPressed(true);
            return;
        }

        if (!isMouseDown) {
            return;
        }

        while (stopwatch.hasTimeElapsed(currentDelay, TimeUnit.MILLISECONDS, true)) {
            click();
            currentDelay = getNextDelay();
        }
    };

    private void click() {
        mc.options.attackKey.setPressed(true);
        KeyBinding.onKeyPressed(((BoundKeyAccessor) mc.options.attackKey).getBoundKey());
        mc.options.attackKey.setPressed(false);
    }

    private long getNextDelay() {
        int cps = MathUtil.nextInt(minCPS.getValue(), maxCPS.getValue() + 1);
        long baseDelay = 1000L / cps;
        return baseDelay + MathUtil.nextInt(-3, 4);
    }
}
