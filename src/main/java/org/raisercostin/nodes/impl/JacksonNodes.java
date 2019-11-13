package org.raisercostin.nodes.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.raisercostin.nodes.Nodes;

public interface JacksonNodes extends Nodes {

  @SneakyThrows
  @Override
  default <T> String toString(T value) {
    return mapper().writeValueAsString(value);
  }

  @Override
  default <T> T toObject(String content, Class<T> clazz) {
    try {
      return mapper().readValue(content, clazz);
    } catch (IOException e) {
      throw new RuntimeException("Cannot deserialize from [" + content + "]: " + e.getMessage(), e);
    }
  }

  /** In case jackson is used and more flexibility is needed. */
  <T extends ObjectMapper> T mapper();

  default <T extends Nodes> T excluding(String... excludedFields) {
    final ObjectMapper mapper = mapper().copy();
    JacksonUtils.configureExclusions(mapper, excludedFields);
    return newNodes(mapper);
  }

  default <T extends Nodes> T parseWithFailOnUnknwon() {
    return newNodes(JacksonUtils.configure(mapper().copy(), true));
  }

  <T extends JacksonNodes> T newNodes(ObjectMapper configure);
}
