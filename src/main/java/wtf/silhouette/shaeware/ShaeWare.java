package wtf.silhouette.shaeware;

import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;

@Getter
public final class ShaeWare implements ClientModInitializer {

    @Getter
    private static ShaeWare instance;

    private final String name, version, modId;

    public ShaeWare() {
        instance = this;

        this.name = "ShaeWare";
        this.version = "1.0.0";
        this.modId = name.toLowerCase();
    }

    @Override
    public void onInitializeClient() {

    }
}
