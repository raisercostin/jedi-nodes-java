package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YmlUtils2 implements JacksonNodes, JacksonNodesLike<YmlUtils2, YAMLMapper, FormatSchema> {
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

  @Override
  public YmlUtils2 newNodes(YAMLMapper configure) {
    return new YmlUtils2(configure);
  }
}
