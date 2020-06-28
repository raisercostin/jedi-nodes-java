package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonUtils2 implements JacksonNodes, JacksonNodesLike<JsonUtils2, JsonMapper> {
  public final JsonMapper mapper;

  public JsonUtils2() {
    this(JacksonUtils.configure(new JsonMapper()));
  }

  public JsonUtils2(JsonMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonMapper mapper() {
    return mapper;
  }

  @Override
  public JsonUtils2 newNodes(JsonMapper mapper) {
    return new JsonUtils2(mapper);
  }
}
