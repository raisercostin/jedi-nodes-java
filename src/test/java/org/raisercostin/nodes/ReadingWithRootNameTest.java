package org.raisercostin.nodes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReadingWithRootNameTest {
  static class Dummy1 {
    String field1;
    String field2;
    Dummy2 field3;
  }

  static class Dummy2 {
    String field4;
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
      .withPrefix("prefix1")
      .toObject("prefix1.field1:value1\nprefix1.field2:value2", Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
  }

  @Test
  void testParseWithHierarchicalPrefix() {
    Dummy1 d1 = Nodes.prop.withIgnoreUnknwon()
      .withPrefix("prefix1.prefix2")
      .toObject("prefix1.prefix2.field1:value1\nprefix1.prefix2.field2:value2", Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
  }

  @Test
  void testParseWithHierarchicalPrefixAndSubobject() {
    Dummy1 d1 = Nodes.prop.withIgnoreUnknwon()
      .withPrefix("prefix1.prefix2")
      .toObject(
        "prefix1.prefix2.field1:value1\nprefix1.prefix2.field2:value2\nprefix1.prefix2.field3.field4:value4\nprefix1.prefix2.field3.field5:value5",
        Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
    assertThat(d1.field3.field4).isEqualTo("value4");
  }

  @Test
  void testParseWithPrefixAndIgnorePropertiesWithoutPrefix() {
    Dummy1 d1 = Nodes.prop.withIgnoreUnknwon()
      .withPrefix("prefix1")
      .toObject("prefix1.field1:value1\nprefix1.field2:value2\nfield1:value3", Dummy1.class);
    assertThat(d1.field1).isEqualTo("value1");
    assertThat(d1.field2).isEqualTo("value2");
  }

  @Test
  void testWithEmptyString() {
    Dummy1 d0 = Nodes.prop.withIgnoreUnknwon().toObject("", Dummy1.class);
    assertThat(d0.field1).isNull();
    assertThat(d0.field2).isNull();
    Dummy1 d1 = Nodes.prop.withIgnoreUnknwon().withPrefix("prefix1").toObject("", Dummy1.class);
    assertThat(d1.field1).isNull();
    assertThat(d1.field2).isNull();
  }
}
