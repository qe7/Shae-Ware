package wtf.silhouette.shaeware.core.feature.module;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.reflections.Reflections;
import wtf.silhouette.shaeware.ShaeWare;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.bus.Handler;
import wtf.silhouette.shaeware.core.bus.Listener;
import wtf.silhouette.shaeware.core.bus.SubscribeEvent;
import wtf.silhouette.shaeware.core.bus.impl.game.InitializeEvent;
import wtf.silhouette.shaeware.core.bus.impl.game.KeyPressEvent;
import wtf.silhouette.shaeware.core.bus.impl.game.ShutdownEvent;
import wtf.silhouette.shaeware.core.feature.module.mode.AbstractModuleMode;
import wtf.silhouette.shaeware.core.feature.module.settings.AbstractSetting;
import wtf.silhouette.shaeware.core.feature.module.settings.impl.ModeSetting;
import wtf.silhouette.shaeware.core.manager.Manager;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;
import wtf.silhouette.shaeware.utils.FileUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public final class ModuleManager extends Manager<Class<? extends AbstractModule>, AbstractModule> implements Handler {

    private final Map<AbstractModule, List<AbstractSetting<?>>> settings = new HashMap<>();

    public ModuleManager() {
        this.loadModules();

        ManagerFactory.get(EventManager.class).registerHandler(this);
    }

    public void loadModules() {
        Set<Class<? extends AbstractModule>> discovered = new Reflections("wtf.silhouette.shaeware.features.modules").getSubTypesOf(AbstractModule.class);

        for (Class<? extends AbstractModule> clazz : discovered) {
            try {
                AbstractModule module = clazz.getDeclaredConstructor().newInstance();
                add(clazz, module);
                ManagerFactory.get(EventManager.class).registerHandler(module);

                for (Field declaredField : module.getClass().getDeclaredFields()) {
                    if (declaredField.getType().getSuperclass() != null && declaredField.getType().getSuperclass().equals(AbstractSetting.class)) {
                        if (!declaredField.trySetAccessible()) {
                            continue;
                        }

                        try {
                            this.registerAbstractSetting(module, (AbstractSetting<?>) declaredField.get(module));

                            if (declaredField.get(module) instanceof ModeSetting) {
                                for (AbstractModuleMode<?> mode : ((ModeSetting) declaredField.get(module)).getModes()) {
                                    mode.getAbstractSettings().forEach(property -> this.registerAbstractSetting(module, property));
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void initialize() {
        final String defaultConfig = FileUtil.readFile("default");

        if (defaultConfig != null) {
            JsonObject object = ShaeWare.getInstance().getGson().fromJson(defaultConfig, JsonObject.class);

            for (AbstractModule module : this.map.values()) {
                if (object.has(module.getName())) {
                    module.deserialize(object.get(module.getName()).getAsJsonObject());
                }
            }
        }
    }

    public void shutdown() {
        final JsonObject object = new JsonObject();

        for (AbstractModule module : this.map.values()) {
            object.add(module.getName(), module.serialize());
        }

        FileUtil.writeFile("default", object.toString());
    }

    public AbstractModule getByName(String name) {
        for (AbstractModule module : map.values()) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public List<AbstractModule> getEnabledModules() {
        return map.values().stream().filter(AbstractModule::isEnabled).collect(Collectors.toList());
    }

    public List<AbstractModule> getModulesByCategory(ModuleCategory category) {
        return map.values().stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public Collection<AbstractModule> getAllModules() {
        return map.values();
    }

    public void registerAbstractSetting(final AbstractModule module, final AbstractSetting<?> setting) {
        settings.putIfAbsent(module, new ArrayList<>());
        settings.get(module).add(setting);
    }

    public List<AbstractSetting<?>> getSettingsByModule(AbstractModule module) {
        return settings.getOrDefault(module, Collections.emptyList());
    }

    @SubscribeEvent
    private final Listener<KeyPressEvent> keyPressEventListener = event -> {
        final int key = event.keyCode();

        if (key == -1 || (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) != GLFW.GLFW_PRESS) || MinecraftClient.getInstance().currentScreen != null) {
            return;
        }

        for (AbstractModule module : this.map.values()) {
            if (module.getKeyBind() == key) {
                module.setEnabled(!module.isEnabled());
            }
        }
    };

    @SubscribeEvent
    private final Listener<InitializeEvent> initializeEventListener = event -> this.initialize();

    @SubscribeEvent
    private final Listener<ShutdownEvent> shutdownEventListener = event -> this.shutdown();
}
