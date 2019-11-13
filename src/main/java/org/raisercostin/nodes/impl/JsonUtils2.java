package org.raisercostin.nodes.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.vavr.collection.Iterator;
import io.vavr.collection.Stream;

public class JsonUtils2 implements JacksonNodes {
  public final JsonMapper mapper;

  public JsonUtils2() {
    this(JacksonUtils.configure(new JsonMapper()));
  }

  public JsonUtils2(JsonMapper mapper) {
    this.mapper = mapper;
  }

  public <T> Stream<T> fromJsonAsArray(String value, Class<T> clazz) {
    try {
      return (Stream<T>) Iterator.ofAll(mapper.readerFor(clazz).readValues(value)).toStream();
    } catch (IOException e) {
      throw new RuntimeException("Cannot deserialize from [" + value + "]: " + e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonMapper mapper() {
    return mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonUtils2 newNodes(ObjectMapper mapper) {
    return new JsonUtils2((JsonMapper) mapper);
  }
}
