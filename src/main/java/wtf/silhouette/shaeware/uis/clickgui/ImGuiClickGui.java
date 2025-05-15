package wtf.silhouette.shaeware.uis.clickgui;

import imgui.app.Application;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ImGuiClickGui extends Screen /*implements IHandler*/ {

    public static final ImGuiClickGui INSTANCE = new ImGuiClickGui();

    private final ImGuiApplication application = new ImGuiApplication();

    public ImGuiClickGui() {
        super(Text.of("ImGui"));
//        ExampleMod.INSTANCE.getEventBus().registerHandler(this);
    }

    public void initialize() {
        Application.launch(application);
    }

//    @SubscribeEvent
//    private final Listener<Render2DEvent> render2DEventListener = event -> {
//        if(MinecraftClient.getInstance().currentScreen != this) return;
//        application.runFrame();
//    };
}
