package wtf.silhouette.shaeware.core.feature.module.mode;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import wtf.silhouette.shaeware.core.bus.Handler;
import wtf.silhouette.shaeware.core.feature.module.AbstractModule;
import wtf.silhouette.shaeware.core.feature.module.settings.AbstractSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class AbstractModuleMode<T extends AbstractModule> implements Handler {

  protected final MinecraftClient mc = MinecraftClient.getInstance();

  @Getter
  private final List<AbstractSetting<?>> abstractSettings = new ArrayList<>();

  @Getter
  protected final T module;
  @Setter
  private BooleanSupplier supplier;
  @Getter
  private final String name;
  private boolean lastState;

  public AbstractModuleMode(T module, String name) {
    this.module = module;
    this.name = name;
  }

  public void onEnable() {
  }

  public void onDisable() {
  }

  @Override
  public boolean listening() {
    boolean flag = module.isEnabled() && supplier.getAsBoolean();

    if (lastState != flag) {
      if (flag) {
        onEnable();
      } else {
        onDisable();
      }

      lastState = flag;
    }

    return flag;
  }
}
