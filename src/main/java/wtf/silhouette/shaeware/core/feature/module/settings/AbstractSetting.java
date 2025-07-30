package wtf.silhouette.shaeware.core.feature.module.settings;

import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.config.Serialized;

import java.util.function.BooleanSupplier;

public abstract class AbstractSetting<T> implements Serialized {
    @Getter
    private final String name;
    @Setter
    @Getter
    private T value;

    private BooleanSupplier supplier;

    public AbstractSetting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public boolean shouldHide() {
        return supplier != null && !supplier.getAsBoolean();
    }

    public <V extends AbstractSetting<?>> V supplyIf(BooleanSupplier supplier) {
        this.supplier = supplier;
        return (V) this;
    }
}
