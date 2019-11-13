package org.raisercostin.nodes;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class NodesTest {
  private static class SampleAddress {
    public String country = "Romania";
    public String city = "Bucharest";
  }

  private static class SamplePerson {
    public String name = "Taleb";
    public int age = 18;
    public OffsetDateTime birthdate = OffsetDateTime.of(1990, 1, 2, 3, 4, 5, 6, ZoneOffset.UTC);
    public SampleAddress address = new SampleAddress();
  }

  @Test
  void testCsv() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.csv.excluding("address");
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("address,age,birthdate,name\n,18,\"1990-01-02T03:04:05.000000006Z\",Taleb\n");
  }

  @Test
  void testProperties() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.prop;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("name=Taleb\n" + "age=18\n" + "birthdate=1990-01-02T03:04:05.000000006Z\n"
        + "address.country=Romania\n" + "address.city=Bucharest\n");
  }

  @Test
  void testYaml() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.yml;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("---\n" + "name: \"Taleb\"\n" + "age: 18\n" + "birthdate: \"1990-01-02T03:04:05.000000006Z\"\n"
        + "address:\n" + "  country: \"Romania\"\n" + "  city: \"Bucharest\"\n");
  }

  @Test
  void testXml() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.xml;
    System.out.println(nodes.toString(a));
    assertThat(normalize(nodes.toString(a))).isEqualTo(normalize("<SamplePerson>\n" + "  <name>Taleb</name>\n" + "  <age>18</age>\n"
        + "  <birthdate>1990-01-02T03:04:05.000000006Z</birthdate>\n" + "  <address>\n" + "    <country>Romania</country>\n"
        + "    <city>Bucharest</city>\n" + "  </address>\n" + "</SamplePerson>\n"));
  }

  @Test
  void testJson() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.json;
    System.out.println(nodes.toString(a));
    assertThat(normalize(nodes.toString(a))).isEqualTo(
        normalize("{\n" + "  \"name\" : \"Taleb\",\n" + "  \"age\" : 18,\n" + "  \"birthdate\" : \"1990-01-02T03:04:05.000000006Z\",\n"
            + "  \"address\" : {\n" + "    \"country\" : \"Romania\",\n" + "    \"city\" : \"Bucharest\"\n" + "  }\n" + "}"));
  }

  private String normalize(String content) {
    return content.replaceAll("\r", "").replaceAll("\n", "\\\\n\n");
  }

  private String normalizePreserveLineFeed(String content) {
    return content.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n\n");
  }

  @Test
  void testGson() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.gson;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a))
        .isEqualTo("{\n" + "  \"name\": \"Taleb\",\n" + "  \"age\": 18,\n" + "  \"birthdate\": \"1990-01-02T03:04:05.000000006Z\",\n"
            + "  \"address\": {\n" + "    \"country\": \"Romania\",\n" + "    \"city\": \"Bucharest\"\n" + "  }\n" + "}");
  }

  @Test
  @Disabled // jxb needs @XmlRootElement
  void testJxb() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.jxb;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualToNormalizingNewlines("<SamplePerson>\n" + "  <name>Taleb</name>\n" + "  <age>18</age>\n"
        + "  <birthdate>1990-01-02T03:04:05.000000006Z</birthdate>\n" + "  <address>\n" + "    <country>Romania</country>\n"
        + "    <city>Bucharest</city>\n" + "  </address>\n" + "</SamplePerson>\n");
  }
}
