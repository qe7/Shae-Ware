package wtf.silhouette.shaeware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.silhouette.shaeware.components.RotationComponent;
import wtf.silhouette.shaeware.core.bus.EventManager;
import wtf.silhouette.shaeware.core.bus.impl.game.InitializeEvent;
import wtf.silhouette.shaeware.core.bus.impl.game.ShutdownEvent;
import wtf.silhouette.shaeware.core.component.ComponentFactory;
import wtf.silhouette.shaeware.core.feature.module.ModuleManager;
import wtf.silhouette.shaeware.core.manager.ManagerFactory;

@Getter
public class ShaeWare implements ClientModInitializer {

    @Getter
    private static ShaeWare instance;

    private final String name, version, modId;

    private final Gson gson;

    private final Logger logger;

    private boolean initialized = false;

    public ShaeWare() {
        instance = this;

        this.name = "XIII";
        this.version = "1.0.0";
        this.modId = name.toLowerCase().replace(" ", "");

        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.logger = LoggerFactory.getLogger(this.getModId());

        ManagerFactory.register(EventManager.class, new EventManager());
        ManagerFactory.register(ModuleManager.class, new ModuleManager());

        ComponentFactory.register(RotationComponent.class, new RotationComponent());
    }

    @Override
    public void onInitializeClient() {
        ManagerFactory.get(EventManager.class).publishEvent(new InitializeEvent());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> ManagerFactory.get(EventManager.class).publishEvent(new ShutdownEvent())));

        this.initialized = true;
    }
}
