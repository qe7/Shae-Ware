package wtf.silhouette.shaeware.features.modules.impl.movement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wtf.silhouette.shaeware.features.components.impl.RotationComponent;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.EnumSetting;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.interfaces.IEnumSetting;

public final class MovementFixModule extends AbstractModule {

  @Getter
  private static final EnumSetting<FixMode> fixModeEnumSetting = new EnumSetting<>("Mode", FixMode.OFF);

  public MovementFixModule() {
    super("Movement Fix", "Makes the clients rotations always abide by Minecraft's strafing.", ModuleCategory.MOVEMENT);
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
