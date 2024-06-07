package org.raisercostin.nodes.impl;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.raisercostin.nodes.Nodes;

class XmlJxbNodesTest {

  @NoArgsConstructor
  @AllArgsConstructor
  @XmlRootElement
  public static class Dummy {
    public String foo;
  }

  @Test
  void testToString() {
    assertThat(Nodes.xmlJxb.toString(new Dummy("mytext")))
      .contains("<foo>mytext</foo>");
  }

  @Test
  @Disabled("for now it doesn't work")
  void testToStringWithoutRoot() {
    assertThat(Nodes.xmlJxb.toString("text"))
      .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><dummy/>");
  }

  @Test
  void testToObject() {
    assertThat(Nodes.xmlJxb.toObject(
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<dummy><foo>mytext2</foo></dummy>\n",
      Dummy.class).foo).isEqualTo("mytext2");
  }
}
