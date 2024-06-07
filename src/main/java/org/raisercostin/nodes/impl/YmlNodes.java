package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vavr.Lazy;
import org.raisercostin.nodes.JacksonNodes;

public class YmlNodes implements JacksonNodes, JacksonNodesLike<YmlNodes, YAMLMapper, FormatSchema> {
  private final Lazy<YAMLMapper> mapper;

  public YmlNodes() {
    this(Lazy.of(() -> JacksonUtils.configure(new YAMLMapper())));
  }

  public YmlNodes(Lazy<YAMLMapper> mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public YAMLMapper mapper() {
    return mapper.get();
  }

  @Override
  public YmlNodes create(YAMLMapper configure) {
    return new YmlNodes(Lazy.of(() -> configure));
  }

  @SuppressWarnings("unchecked")
  @Override
  public YmlNodes createJacksonNodes(ObjectMapper mapper) {
    return create((YAMLMapper) mapper);
  }
}
