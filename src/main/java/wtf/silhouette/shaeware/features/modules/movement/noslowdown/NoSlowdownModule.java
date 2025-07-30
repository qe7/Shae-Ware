package wtf.silhouette.shaeware.features.modules.movement.noslowdown;

import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.features.modules.movement.noslowdown.impl.VanillaNoSlowdownMode;
import wtf.silhouette.shaeware.features.modules.movement.noslowdown.impl.WatchdogNoSlowdownMode;

public final class NoSlowdownModule extends AbstractModule {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", new VanillaNoSlowdownMode(this, "Vanilla"), new WatchdogNoSlowdownMode(this, "Watchdog"));

    public NoSlowdownModule() {
        super("No Slowdown", "Prevent the player from slowing down.", ModuleCategory.MOVEMENT);
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> onScreen = event -> this.setSuffix(this.mode.getValue().getName());
}
