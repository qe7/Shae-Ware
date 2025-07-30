package wtf.silhouette.shaeware.features.modules.combat.velocity;

import lombok.Getter;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.features.modules.combat.velocity.impl.JumpVelocityMode;
import wtf.silhouette.shaeware.features.modules.combat.velocity.impl.PacketVelocityMode;

public final class VelocityModule extends AbstractModule {

    @Getter
    private final ModeSetting mode = new ModeSetting("Mode", "Packet", new PacketVelocityMode(this, "Packet"), new JumpVelocityMode(this, "Jump"));

    public VelocityModule() {
        super("Velocity", "Reduced or removes velocity.", ModuleCategory.COMBAT);
    }

    @SubscribeEvent
    private final Listener<ScreenEvent> screenEventListener = event -> {
        if (mode.getValue() instanceof PacketVelocityMode) return;
        this.setSuffix(mode.getValue().getName());
    };
}
