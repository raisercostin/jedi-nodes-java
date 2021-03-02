package org.raisercostin.nodes.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.raisercostin.nodes.Nodes;

class PropNodesTest {

  @Value
  private static class P1 {
    public List<String> all;
  }

  @Value
  private static class P2 {
    public P1 p1;
  }

  @Test
  void testObjectContainingArray() {
    P2 p2 = new P2(new P1(API.List("a", "b")));
    assertThat(Nodes.prop.toString(p2)).isEqualTo("p1.all.1=a\np1.all.2=b\n");
  }

  @Test
  void testMapContainingArray() {
    Map<String, Object> p2 = API.Map("key1", new P1(API.List("a", "b")));
    assertThat(Nodes.prop.toString(p2)).isEqualTo("key1.all.1=a\nkey1.all.2=b\n");
  }
}
