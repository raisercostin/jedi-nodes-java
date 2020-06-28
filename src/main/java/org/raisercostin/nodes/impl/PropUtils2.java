package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

public class PropUtils2 implements JacksonNodes, JacksonNodesLike<PropUtils2, JavaPropsMapper> {
  private final JavaPropsMapper mapper;

  public PropUtils2() {
    this(JacksonUtils.configure(new JavaPropsMapper()));
  }

  public PropUtils2(JavaPropsMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JavaPropsMapper mapper() {
    return mapper;
  }

  @Override
  public PropUtils2 newNodes(JavaPropsMapper mapper) {
    return new PropUtils2(mapper);
  }
}
