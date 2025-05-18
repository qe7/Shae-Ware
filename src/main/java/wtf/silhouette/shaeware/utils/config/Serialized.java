package wtf.silhouette.shaeware.utils.config;

import com.google.gson.JsonObject;

public interface Serialized {

  JsonObject serialize();

  void deserialize(JsonObject object);
}