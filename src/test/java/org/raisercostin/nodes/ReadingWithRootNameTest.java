package org.raisercostin.nodes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReadingWithRootNameTest {
  static class Dummy1 {
    String field1;
    String field2;
  }

  @Test
  void test() {
    Dummy1 d1 = Nodes.prop.toObject("field1:value1\nfield2:value2", Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
  }

  @Test
  void testParseWithPrefix() {
    Dummy1 d1 = Nodes.prop.withIgnoreUnknwon()
      .withRootName("prefix1")
      .toObject("prefix1.field1:value1\nprefix1.field2:value2", Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
  }

  @Test
  void testParseWithPrefixAndIgnorePropertiesWithoutPrefix() {
    Dummy1 d1 = Nodes.prop.withIgnoreUnknwon()
      .withRootName("prefix1")
      .toObject("prefix1.field1:value1\nprefix1.field2:value2\nfield1:value3", Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
  }
}
