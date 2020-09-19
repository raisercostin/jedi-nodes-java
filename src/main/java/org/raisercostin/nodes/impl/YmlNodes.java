package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.raisercostin.nodes.JacksonNodes;

public class YmlNodes implements JacksonNodes, JacksonNodesLike<YmlNodes, YAMLMapper, FormatSchema> {
  private final YAMLMapper mapper;

  public YmlNodes() {
    this(JacksonUtils.configure(new YAMLMapper()));
  }

  public YmlNodes(YAMLMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public YAMLMapper mapper() {
    return mapper;
  }

  @Override
  public YmlNodes create(YAMLMapper configure) {
    return new YmlNodes(configure);
  }

  @SuppressWarnings("unchecked")
  @Override
  public YmlNodes createJacksonNodes(ObjectMapper mapper) {
    return create((YAMLMapper) mapper);
  }
}
