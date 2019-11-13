package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YmlUtils2 implements JacksonNodes {
  private final YAMLMapper mapper;

  public YmlUtils2() {
    this(JacksonUtils.configure(new YAMLMapper()));
  }

  public YmlUtils2(YAMLMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public YAMLMapper mapper() {
    return mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public YmlUtils2 newNodes(ObjectMapper configure) {
    return new YmlUtils2((YAMLMapper) configure);
  }
}
