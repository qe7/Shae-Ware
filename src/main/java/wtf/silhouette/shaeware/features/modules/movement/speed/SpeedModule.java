package wtf.silhouette.shaeware.features.modules.movement.speed;

import lombok.Getter;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.features.modules.movement.speed.impl.GroundSpeedMode;
import wtf.silhouette.shaeware.features.modules.movement.speed.impl.GroundStrafeSpeedMode;
import wtf.silhouette.shaeware.features.modules.movement.speed.impl.LegitSpeedMode;

public final class SpeedModule extends AbstractModule {

    @Getter
    private final ModeSetting mode = new ModeSetting("Mode", "Legit",
            new LegitSpeedMode(this, "Legit"),
            new GroundSpeedMode(this, "Ground"),
            new GroundStrafeSpeedMode(this, "Ground Strafe")
    );

    public SpeedModule() {
        super("Speed", "Modifies the player's velocity to make movement faster.", ModuleCategory.MOVEMENT);
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> screenEventListener = event -> {
        this.setSuffix(mode.getValue().getName());
    };
}
