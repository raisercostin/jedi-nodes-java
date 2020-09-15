package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.raisercostin.nodes.JacksonNodes;

public class JsonNodes implements JacksonNodes, JacksonNodesLike<JsonNodes, JsonMapper, FormatSchema> {
  public final JsonMapper mapper;

  public JsonNodes() {
    this(JacksonUtils.configure(new JsonMapper()));
  }

  public JsonNodes(JsonMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonMapper mapper() {
    return mapper;
  }

  @Override
  public JsonNodes newNodes(JsonMapper mapper) {
    return new JsonNodes(mapper);
  }
}
