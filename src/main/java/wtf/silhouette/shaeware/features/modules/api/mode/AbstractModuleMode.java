package wtf.silhouette.shaeware.features.modules.api.mode;

import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.events.api.Handler;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.settings.AbstractSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class AbstractModuleMode<T extends AbstractModule> implements Handler {

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
