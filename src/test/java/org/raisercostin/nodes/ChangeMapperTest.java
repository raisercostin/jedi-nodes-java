package org.raisercostin.nodes;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.raisercostin.nodes.impl.JsonNodes;

class ChangeMapperTest {
  @Test
  void testChangeMapperOnJsonNodes() {
    JsonNodes nodes = Nodes.json;
    nodes.withMapper(mapper -> (JsonMapper) mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true));
  }

  @Test
  void testChangeMapperOnGenericJacksonNodes() {
    JacksonNodes nodes = Nodes.json;
    nodes.withObjectMapper(mapper -> mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true));
  }
}
