package org.raisercostin.nodes;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class NodesTest {
  public static class SampleAddress {
    public SampleAddress() {}
    public SampleAddress(String country, String city) {
      this.country=country;
      this.city=city;
    }
    public String country = "Romania";
    public String city = "Bucharest";
  }

  public static class SamplePerson {
    public String name = "Taleb";
    public int age = 18;
    public OffsetDateTime birthdate = OffsetDateTime.of(1990, 1, 2, 3, 4, 5, 6, ZoneOffset.UTC);
    public SampleAddress address = new SampleAddress();
  }

  @Test
  @Disabled
  //TODO still doesn't deserialize the yaml inside csv
  void testCsv() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.csv.excluding("address");
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("address,age,birthdate,name\n,18,\"1990-01-02T03:04:05.000000006Z\",Taleb\n");
    testDeserialization(nodes, a);
  }

  @Test
  void testCsv2() {
    final Nodes nodes = Nodes.csv;
    testDeserialization(nodes, new SampleAddress());
  }

  @Test
  void testCsvOneLevelObjectAsList() {
    List<SampleAddress> a = Nodes.csv.toList("city,country\nBucharest,Romania", SampleAddress.class);
    assertThat(a.get(0)).usingRecursiveComparison().isEqualTo(new SampleAddress());
  }

  @Test
  void testCsvOneLevelObjectAsListWithTwoObjects() {
    List<SampleAddress> a = Nodes.csv.toList("city,country\nBucharest,Romania\nBucharest2,Romania2", SampleAddress.class);
    assertThat(a.get(0)).usingRecursiveComparison().isEqualTo(new SampleAddress());
    assertThat(a.get(1)).usingRecursiveComparison().isEqualTo(new SampleAddress("Romania2","Bucharest2"));
  }

  @Test
  void testCsvOneLevelObjectAsObject() {
    SampleAddress a = Nodes.csv.toObject("city,country\nBucharest,Romania", SampleAddress.class);
    assertThat(a).usingRecursiveComparison().isEqualTo(new SampleAddress());
  }

  @Test
  void testCsvOneLevelObjectAsTwoObjects() {
    //TODO should complain that not all content was parsed
    SampleAddress a = Nodes.csv.toObject("city,country\nBucharest,Romania\nBucharest2,Romania2", SampleAddress.class);
    assertThat(a).usingRecursiveComparison().isEqualTo(new SampleAddress());
  }

  @Test
  @Disabled
  //TODO still doesn't deserialize the yaml inside csv
  void testCsvEnhancedWithYml() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.csv;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("address,age,birthdate,name\n" + 
        "\"---\n" + 
        "country: \"\"Romania\"\"\n" + 
        "city: \"\"Bucharest\"\"\n" + 
        "\",18,\"1990-01-02T03:04:05.000000006Z\",Taleb\n");
    testDeserialization(nodes, a);
  }

  @Test
  @Disabled
  //TODO still doesn't deserialize the yaml inside csv
  void testCsvEnhancedWithYmlAndBack() {
    testDeserialization(Nodes.csv, new SamplePerson());
  }

  @Test
  void testProperties() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.prop;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("name=Taleb\n" + "age=18\n" + "birthdate=1990-01-02T03:04:05.000000006Z\n"
        + "address.country=Romania\n" + "address.city=Bucharest\n");
    testDeserialization(nodes, a);
  }

  @Test
  void testYaml() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.yml;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a)).isEqualTo("---\n" + "name: \"Taleb\"\n" + "age: 18\n" + "birthdate: \"1990-01-02T03:04:05.000000006Z\"\n"
        + "address:\n" + "  country: \"Romania\"\n" + "  city: \"Bucharest\"\n");
    testDeserialization(nodes, a);
  }

  @Test
  void testXml() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.xml;
    System.out.println(nodes.toString(a));
    assertThat(normalize(nodes.toString(a))).isEqualTo(normalize("<SamplePerson>\n" + "  <name>Taleb</name>\n" + "  <age>18</age>\n"
        + "  <birthdate>1990-01-02T03:04:05.000000006Z</birthdate>\n" + "  <address>\n" + "    <country>Romania</country>\n"
        + "    <city>Bucharest</city>\n" + "  </address>\n" + "</SamplePerson>\n"));
    testDeserialization(nodes, a);
  }

  @Test
  void testJson() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.json;
    System.out.println(nodes.toString(a));
    assertThat(normalize(nodes.toString(a))).isEqualTo(
        normalize("{\n" + "  \"name\" : \"Taleb\",\n" + "  \"age\" : 18,\n" + "  \"birthdate\" : \"1990-01-02T03:04:05.000000006Z\",\n"
            + "  \"address\" : {\n" + "    \"country\" : \"Romania\",\n" + "    \"city\" : \"Bucharest\"\n" + "  }\n" + "}"));
    testDeserialization(nodes, a);
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
    testDeserialization(nodes, a);
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
    testDeserialization(nodes, a);
  }

  @Test
  void testAll() {
    SamplePerson a = new SamplePerson();
    System.out.println("CSV\n"+Nodes.csv.excluding("address").toString(a));
    System.out.println("CSV with YML\n"+Nodes.csv.toString(a));
    System.out.println("GSON\n"+Nodes.gson.toString(a));
    System.out.println("JSON\n"+Nodes.json.toString(a));
    System.out.println("PROP\n"+Nodes.prop.toString(a));
    System.out.println("XML\n"+Nodes.xml.toString(a));
    System.out.println("YML\n"+Nodes.yml.toString(a));
  }

  private void testDeserialization(Nodes nodes, Object o) {
    String expected = nodes.toString(o);
    //System.out.println(expected);
    String actual = nodes.toString(nodes.toObject(expected, o.getClass()));
    assertThat(actual).isEqualTo(expected);
  }
}
