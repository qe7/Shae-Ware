package wtf.silhouette.shaeware.uis.clickgui;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;

public class ImGuiApplication extends Application {


    @Override
    public void process() {
        if (ImGui.begin("Sunshine Client", ImGuiWindowFlags.NoCollapse)) {

            ImGui.end();
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