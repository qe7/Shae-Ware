package wtf.silhouette.shaeware.uis.clickgui;

import imgui.app.Application;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import wtf.silhouette.shaeware.events.api.Handler;
import wtf.silhouette.shaeware.features.modules.impl.visual.ClickGUIModule;
import wtf.silhouette.shaeware.managers.api.ManagerFactory;
import wtf.silhouette.shaeware.managers.impl.EventManager;
import wtf.silhouette.shaeware.managers.impl.ModuleManager;
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
