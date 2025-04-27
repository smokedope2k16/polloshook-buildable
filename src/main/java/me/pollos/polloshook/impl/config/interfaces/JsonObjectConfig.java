package me.pollos.polloshook.impl.config.interfaces;

import com.google.gson.JsonObject;

public interface JsonObjectConfig<T> {
   JsonObject toJsonObject();

   T fromJsonObject(JsonObject var1);
}
