package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Function1;

public interface JacksonNodesLike<SELF extends JacksonNodes, MAPPER extends ObjectMapper> {
  /** In case jackson is used and more flexibility is needed. */
  MAPPER mapper();

  SELF newNodes(MAPPER configure);

  /** In case jackson is used and more flexibility is needed. */
  //<T extends ObjectMapper> T mapper();

  default SELF excluding(String... excludedFields) {
    @SuppressWarnings("unchecked")
    final MAPPER mapper = (MAPPER) mapper().copy();
    JacksonUtils.configureExclusions(mapper, excludedFields);
    return newNodes(mapper);
  }

  @SuppressWarnings("unchecked")
  default SELF parseWithFailOnUnknwon() {
    return newNodes((MAPPER) JacksonUtils.configure(mapper().copy(), true));
  }

  default SELF withMapper(Function1<MAPPER, MAPPER> mapperChanger) {
    final MAPPER mapper = mapperChanger.apply(mapper());
    return newNodes(mapper);
  }

  @SuppressWarnings("unchecked")
  default SELF withCopyMapper() {
    return withMapper(mapper -> (MAPPER) mapper.copy());
  }

  @SuppressWarnings("unchecked")
  default SELF withIgnoreUnknwon() {
    return withMapper(mapper -> (MAPPER) mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
  }

  default SELF withRootName(String rootName) {
    return withMapper(mapper -> {
      mapper.setConfig(mapper.getDeserializationConfig().withRootName(rootName));
      return mapper;
    });
  }
}
