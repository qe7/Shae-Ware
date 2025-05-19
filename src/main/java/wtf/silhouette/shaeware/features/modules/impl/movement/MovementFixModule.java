package wtf.silhouette.shaeware.features.modules.impl.movement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wtf.silhouette.shaeware.events.api.Listener;
import wtf.silhouette.shaeware.events.api.SubscribeEvent;
import wtf.silhouette.shaeware.events.impl.render.ScreenEvent;
import wtf.silhouette.shaeware.features.components.impl.RotationComponent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.EnumSetting;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.interfaces.IEnumSetting;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.ModuleManager;

public final class MovementFixModule extends AbstractModule {

  private static final EnumSetting<FixMode> mode = new EnumSetting<>("Mode", FixMode.OFF);

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
    OFF("Off", RotationComponent.MovementFixMode.OFF),
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
