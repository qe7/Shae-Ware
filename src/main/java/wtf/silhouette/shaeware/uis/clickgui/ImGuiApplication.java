package wtf.silhouette.shaeware.uis.clickgui;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.features.modules.api.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.features.modules.api.settings.AbstractSetting;
import wtf.silhouette.shaeware.features.modules.api.settings.impl.*;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.ModuleManager;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ImGuiApplication extends Application {

  @Getter
  private boolean listeningForKey = false;
  private AbstractModule currentKeyListeningModule = null;

  private final ImInt selectedCategory = new ImInt(0);
  private final ImInt selectedModule = new ImInt(0);

  @Override
  public void process() {
    if (!ShaeWare.getInstance().isInitialized()) {
      return;
    }

    if (ImGui.begin(ShaeWare.getInstance().getName(), ImGuiWindowFlags.NoCollapse)) {
      ImGui.columns(3, "main_columns", false);
      ImGui.setColumnWidth(0, 150);
      ImGui.setColumnWidth(1, 150);
      ImGui.setColumnWidth(2, ImGui.getWindowWidth() - 300);

      ImGui.beginChild("categories_panel", 0, 0, true);
      ModuleCategory[] categories = ModuleCategory.values();
      for (int i = 0; i < categories.length; i++) {
        boolean isSel = (selectedCategory.get() == i);
        if (ImGui.selectable(categories[i].getName(), isSel)) {
          selectedCategory.set(i);
          selectedModule.set(0);
        }
      }
      ImGui.endChild();
      ImGui.nextColumn();

      ImGui.beginChild("modules_panel", 0, 0, true);
      List<AbstractModule> modules = ManagerFactory.get(ModuleManager.class).getModulesByCategory(categories[selectedCategory.get()]);
      modules.sort(Comparator.comparing(AbstractModule::getName));

      for (int i = 0; i < modules.size(); i++) {
        AbstractModule mod = modules.get(i);
        boolean isSel = (selectedModule.get() == i);
        if (ImGui.selectable(mod.getName(), isSel)) {
          selectedModule.set(i);
        }
      }
      ImGui.endChild();
      ImGui.nextColumn();

      ImGui.beginChild("settings_panel", 0, 0, true);
      int modIndex = selectedModule.get();
      if (modIndex >= 0 && modIndex < modules.size()) {
        AbstractModule active = modules.get(modIndex);
        ImGui.pushID(active.getName());
        ImGui.text("Module: " + active.getName());
        ImGui.separator();

        if (ImGui.checkbox("Enabled", active.isEnabled())) active.toggle();
        ImGui.sameLine();
        if (ImGui.checkbox("Drawn", active.isDrawn())) active.setDrawn(!active.isDrawn());

        String keyText = listeningForKey && currentKeyListeningModule == active ? "Press any key..." : "Key: " + (active.getKeyBind() == GLFW.GLFW_KEY_UNKNOWN ? "None" : InputUtil.fromKeyCode(active.getKeyBind(), 0).getLocalizedText().getString());
        ImGui.sameLine();
        if (ImGui.button(keyText)) {
          listeningForKey = !listeningForKey;
          currentKeyListeningModule = listeningForKey ? active : null;
        }

        List<AbstractSetting<?>> settings = ManagerFactory.get(ModuleManager.class).getSettingsByModule(active);
        if (!settings.isEmpty()) {
          ImGui.separator();
          for (AbstractSetting<?> setting : settings) {
            if (setting.shouldHide()) continue;
            renderSetting(setting);
          }
        }

        ImGui.popID();
      } else {
        ImGui.text("Select a module to see settings.");
      }
      ImGui.endChild();

      ImGui.columns(1);
      ImGui.end();
    }
  }

  @Override
  public void postProcess() {
    if (listeningForKey && currentKeyListeningModule != null) {
      for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) {
        if (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_PRESS) {
          if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
            currentKeyListeningModule.setKeyBind(GLFW.GLFW_KEY_UNKNOWN);
            listeningForKey = false;
            currentKeyListeningModule = null;
            break;
          }
          if (key == GLFW.GLFW_KEY_ESCAPE) {
            listeningForKey = false;
            currentKeyListeningModule = null;
            break;
          }
          currentKeyListeningModule.setKeyBind(key);
          listeningForKey = false;
          currentKeyListeningModule = null;
          break;
        }
      }
    }
  }

  private void renderSetting(AbstractSetting<?> setting) {
    if (setting instanceof BooleanSetting booleanSetting) {
      if (ImGui.checkbox(setting.getName(), booleanSetting.getValue())) {
        booleanSetting.setValue(!booleanSetting.getValue());
      }
    }

    if (setting instanceof IntSetting intSetting) {
      int[] ints = new int[]{intSetting.getValue()};
      ImGui.sliderInt(setting.getName(), ints, intSetting.getMin(), intSetting.getMax());
      intSetting.setValue(ints[0]);
    }

    if (setting instanceof FloatSetting floatSetting) {
      float[] floats = new float[]{floatSetting.getValue()};
      ImGui.sliderFloat(setting.getName(), floats, floatSetting.getMin(), floatSetting.getMax());
      floatSetting.setValue(floats[0]);
    }

    if (setting instanceof ModeSetting modeSetting) {
      String[] combo = new String[modeSetting.getModes().size()];
      ImInt index = new ImInt();

      for (int i = 0; i < modeSetting.getModes().size(); i++) {
        AbstractModuleMode<?> mode = modeSetting.getModes().get(i);
        combo[i] = mode.getName();
        if (mode.equals(modeSetting.getValue())) {
          index.set(i);
        }
      }

      ImGui.combo(modeSetting.getName(), index, combo);
      modeSetting.setValue(modeSetting.getModes().get(index.get()));
    }

    if (setting instanceof EnumSetting<?> enumSetting) {
      String[] combo = new String[enumSetting.getValues().size()];
      ImInt index = new ImInt();

      for (int i = 0; i < enumSetting.getValues().size(); i++) {
        combo[i] = enumSetting.getValueByIndex(i).getName();

        if (combo[i].equals(enumSetting.getValue().getName())) {
          index.set(i);
        }
      }

      ImGui.combo(enumSetting.getName(), index, combo);
      enumSetting.setCastedValue(enumSetting.getValueByIndex(index.get()));
    }

    if (setting instanceof ColorSetting colorSetting) {
      float[] color = new float[]{colorSetting.getValue().getRed() / 255f, colorSetting.getValue().getGreen() / 255f, colorSetting.getValue().getBlue() / 255f, colorSetting.getValue().getAlpha() / 255f};

      if (ImGui.colorEdit4(colorSetting.getName(), color)) {
        colorSetting.setValue(new Color((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255), (int) (color[3] * 255)));
      }
    }

    if (setting instanceof StringSetting stringSetting) {
      ImString imString = new ImString(stringSetting.getValue(), 256);
      if (ImGui.inputText(stringSetting.getName(), imString)) {
        stringSetting.setValue(imString.get());
      }
    }
  }

  @Override
  protected void initWindow(Configuration config) {
    this.handle = MinecraftClient.getInstance().getWindow().getHandle();
  }

  @Override
  public void runFrame() {
    this.startFrame();
    this.preProcess();
    this.process();
    this.postProcess();
    ImGui.render();
    this.imGuiGl3.renderDrawData(ImGui.getDrawData());
  }
}