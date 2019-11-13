package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonUtils2 implements JacksonNodes {
  public final JsonMapper mapper;

  public JsonUtils2() {
    this(JacksonUtils.configure(new JsonMapper()));
  }

  public JsonUtils2(JsonMapper mapper) {
    this.mapper = mapper;
  }

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
