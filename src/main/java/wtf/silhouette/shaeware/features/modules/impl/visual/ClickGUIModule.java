package wtf.silhouette.shaeware.features.modules.impl.visual;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import wtf.silhouette.shaeware.features.modules.api.AbstractModule;
import wtf.silhouette.shaeware.features.modules.api.ModuleCategory;
import wtf.silhouette.shaeware.uis.clickgui.ImGuiClickGui;

public final class ClickGUIModule extends AbstractModule {

  public ClickGUIModule() {
    super("Click GUI", "Displays a clickable interface to manage modules.", ModuleCategory.VISUAL);

    this.setKeyBind(GLFW.GLFW_KEY_RIGHT_SHIFT);
  }

  @Override
  public void onEnable() {
    if (mc.world == null || mc.player == null) {
      this.setEnabled(false);
      return;
    }

    super.onEnable();
    mc.setScreen(ImGuiClickGui.INSTANCE);
  }

  @Override
  public void onDisable() {
    super.onDisable();

    if (mc.currentScreen == ImGuiClickGui.INSTANCE) {
      mc.setScreen(null);
    }
  }
}
