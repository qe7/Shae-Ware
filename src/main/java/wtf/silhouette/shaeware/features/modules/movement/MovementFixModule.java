package wtf.silhouette.shaeware.features.modules.movement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.components.RotationComponent;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.ModuleCategory;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.EnumSetting;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.interfaces.IEnumSetting;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.core.feature.module.ModuleManager;

public final class MovementFixModule extends AbstractModule {

  private static final EnumSetting<FixMode> mode = new EnumSetting<>("Mode", FixMode.ADVANCED);

  public MovementFixModule() {
    super("Movement Fix", "Makes the clients rotations always abide by Minecraft's strafing.", ModuleCategory.MOVEMENT);
  }

  @SubscribeEvent
  private final Listener<ScreenEvent> screenEventListener = event -> {
    this.setSuffix(mode.getValue().getName());
  };

  public static RotationComponent.MovementFixMode getMovementFixMode() {
    if (!ManagerFactory.get(ModuleManager.class).get(MovementFixModule.class).isEnabled()) {
      return RotationComponent.MovementFixMode.OFF;
    }
    return mode.getValue().getMovementFixMode();
  }

  @AllArgsConstructor
  public enum FixMode implements IEnumSetting {
    BASIC("Basic", RotationComponent.MovementFixMode.BASIC),
    ADVANCED("Advanced", RotationComponent.MovementFixMode.ADVANCED);

    private final String name;

    @Getter
    private final RotationComponent.MovementFixMode movementFixMode;

    @Override
    public String getName() {
      return name;
    }
  }
}
