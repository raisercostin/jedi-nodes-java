package org.raisercostin.nodes.impl;

import java.io.IOException;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.vavr.control.Option;
import org.raisercostin.nodes.Nodes;

public class GsonNodes implements Nodes {
  public static Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(new ExclusionStrategy() {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(JsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
      return false;
    }
  }).registerTypeAdapter(OffsetDateTime.class, new TypeAdapter<OffsetDateTime>() {
    @Override
    public void write(JsonWriter out, OffsetDateTime value) throws IOException {
      if (value != null) {
        out.value(value.toString());
      } else {
        out.nullValue();
      }
    }

    @Override
    public OffsetDateTime read(JsonReader in) throws IOException {
      String value = in.nextString();
      if (value != null) {
        return OffsetDateTime.parse(value);
      } else {
        return null;
      }
    }
  }).create();

  public static String prettyPrintJson(String response2) {
    String json = gson.toJson(new JsonParser().parse(response2));
    return json;
  }

  public static <T> Option<T> fromJsonViaGson(String value, Class<T> clazz) {
    if (value.trim().isEmpty()) {
      return Option.none();
    }
    T result = gson.fromJson(value, clazz);
    return Option.of(result);
  }

  @Override
  public <T> String toString(T value) {
    String json = gson.toJson(value);
    return json;
  }

  @Override
  public <T> T toObject(String content, Class<T> clazz) {
    T result = gson.fromJson(content, clazz);
    return result;
  }
}
