package wtf.silhouette.shaeware.core.feature.module;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.config.Serialized;
import wtf.silhouette.shaeware.core.bus.Handler;
import wtf.silhouette.shaeware.core.feature.module.settings.AbstractSetting;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;

@Getter
@Setter
public abstract class AbstractModule implements Toggleable, Serialized, Handler {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected final String name;
    protected final String description;
    protected final ModuleCategory category;

    protected String suffix = "";

    protected boolean enabled;
    protected boolean drawn = true;
    protected boolean hidden = false;

    protected int keyBind = GLFW.GLFW_KEY_UNKNOWN;

    public AbstractModule(final String name, final String description, final ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;

        if (enabled) onEnable();
        else onDisable();
    }

    @Override
    public void toggle() {
        setEnabled(!this.enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean listening() {
        return this.enabled;
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("enabled", this.enabled);
        object.addProperty("drawn", this.drawn);
        object.addProperty("keyBind", this.keyBind);

        if (!ManagerFactory.get(ModuleManager.class).getSettingsByModule(this).isEmpty()) {
            final JsonObject settings = new JsonObject();

            for (AbstractSetting<?> setting : ManagerFactory.get(ModuleManager.class).getSettingsByModule(this)) {
                settings.add(setting.getName(), setting.serialize());
            }

            object.add("settings", settings);
        }

        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.setEnabled(object.get("enabled").getAsBoolean());
        this.setDrawn(object.get("drawn").getAsBoolean());
        this.setKeyBind(object.get("keyBind").getAsInt());

        if (object.has("settings")) {
            final JsonObject settings = object.getAsJsonObject("settings");

            for (AbstractSetting<?> setting : ManagerFactory.get(ModuleManager.class).getSettingsByModule(this)) {
                if (settings.has(setting.getName())) {
                    try {
                        setting.deserialize(settings.getAsJsonObject(setting.getName()));
                    } catch (Exception e) {
                        ShaeWare.getInstance().getLogger().error("Failed to deserialize setting: {}", setting.getName());
                    }
                }
            }
        }
    }
}
