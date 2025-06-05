package wtf.silhouette.shaeware.core.feature.module.settings.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import wtf.silhouette.shaeware.core.feature.module.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.core.feature.module.settings.AbstractSetting;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.core.bus.EventManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class ModeSetting extends AbstractSetting<AbstractModuleMode<?>> {

  private final List<AbstractModuleMode<?>> modes = new ArrayList<>();

  public ModeSetting(String name, String value, AbstractModuleMode<?>... modes) {
    super(name, new ArrayList<>(Arrays.asList(modes)).stream().filter(mode -> mode.getName().equals(value)).findAny().orElseThrow());

    for (AbstractModuleMode<?> mode : modes) {
      mode.setSupplier(() -> mode.getModule().isEnabled() && mode.equals(getValue()));

      this.modes.add(mode);
      ManagerFactory.get(EventManager.class).registerHandler(mode);

      for (Field declaredField : mode.getClass().getDeclaredFields()) {
        if (declaredField.getType().getSuperclass() != null && declaredField.getType().getSuperclass().equals(AbstractSetting.class)) {
          declaredField.trySetAccessible();

          try {
            mode.getAbstractSettings().add((AbstractSetting<?>) declaredField.get(mode));
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        }
      }
    }
  }

  public void cycleForward() {
    int currentIndex = modes.indexOf(getValue());
    if (currentIndex == modes.size() - 1) {
      setValue(modes.getFirst());
    } else {
      setValue(modes.get(currentIndex + 1));
    }
  }

  public void cycleBackward() {
    int currentIndex = modes.indexOf(getValue());
    if (currentIndex == 0) {
      setValue(modes.getLast());
    } else {
      setValue(modes.get(currentIndex - 1));
    }
  }

  @Override
  public JsonObject serialize() {
    final JsonObject object = new JsonObject();

    object.addProperty("value", this.getValue().getName());

    return object;
  }

  @Override
  public void deserialize(JsonObject object) {
    if (!object.has("value") || !object.get("value").isJsonPrimitive()) {
      return;
    }

    final String name = object.get("value").getAsString();

    for (AbstractModuleMode<?> mode : modes) {
      if (mode.getName().equals(name)) {
        this.setValue(mode);
        break;
      }
    }
  }
}
