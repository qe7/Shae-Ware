package wtf.silhouette.shaeware.core.feature.module.settings.impl;

import com.google.gson.JsonObject;
import wtf.silhouette.shaeware.core.feature.module.settings.AbstractSetting;

public final class StringSetting extends AbstractSetting<String> {

    public StringSetting(String name, String value) {
        super(name, value);
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

        this.setValue(object.get("value").getAsString());
    }
}
