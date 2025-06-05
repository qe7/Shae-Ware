package wtf.silhouette.shaeware.platform.gui;

import imgui.app.Application;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import wtf.silhouette.shaeware.core.bus.Handler;
import wtf.silhouette.shaeware.features.modules.client.ClickGUIModule;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.feature.module.ModuleManager;
import wtf.silhouette.shaeware.utils.IMinecraft;

public final class ImGuiClickGui extends Screen implements IMinecraft, Handler {

  public static final ImGuiClickGui INSTANCE = new ImGuiClickGui();

  private final ImGuiApplication application = new ImGuiApplication();

  public ImGuiClickGui() {
    super(Text.of("ImGui"));
    ManagerFactory.get(EventManager.class).registerHandler(this);
  }

  @Override
  public boolean shouldCloseOnEsc() {
    return !application.isListeningForKey();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    application.runFrame();
  }

  @Override
  public void close() {
    super.close();

    ManagerFactory.get(ModuleManager.class).get(ClickGUIModule.class).setEnabled(false);
  }

  public void initialize() {
    Application.launch(application);
  }
}
