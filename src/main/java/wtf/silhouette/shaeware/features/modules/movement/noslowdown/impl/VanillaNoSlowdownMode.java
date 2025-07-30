package wtf.silhouette.shaeware.features.modules.movement.noslowdown.impl;

import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.player.SlowdownEvent;
import wtf.silhouette.shaeware.core.feature.module.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.movement.noslowdown.NoSlowdownModule;

public final class VanillaNoSlowdownMode extends AbstractModuleMode<NoSlowdownModule> {

    public VanillaNoSlowdownMode(NoSlowdownModule module, String name) {
        super(module, name);
    }

    @SubscribeEvent
    private final Listener<SlowdownEvent> onSlowdown = event -> event.setCancelled(true);
}
