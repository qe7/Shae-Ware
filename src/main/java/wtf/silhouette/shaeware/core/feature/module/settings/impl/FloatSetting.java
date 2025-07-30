package wtf.silhouette.shaeware.core.feature.module.settings.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import wtf.silhouette.shaeware.core.feature.module.settings.AbstractSetting;

@Getter
public final class FloatSetting extends AbstractSetting<Float> {

    private final float min, max, increment;

    @Setter
    private String unit;

    public FloatSetting(String name, float value, float min, float max, float increment) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject object = new JsonObject();

        object.addProperty("value", this.getValue());

        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        if (!object.has("value") || !object.get("value").isJsonPrimitive()) {
            return;
        }

        this.setValue(object.get("value").getAsFloat());
    }
}
