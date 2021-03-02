package org.raisercostin.nodes;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.ConfigSyntax;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.raisercostin.jedio.Locations;
import org.raisercostin.nodes.impl.HoconNodes;

class NodesTest {
  public static class SampleAddress {
    public SampleAddress() {
    }

    public SampleAddress(String country, String city) {
      this.country = country;
      this.city = city;
    }

    public String country = "Romania";
    public String city = "Bucharest";
  }

  public static SamplePerson a = new SamplePerson();
  public static SamplePerson b = new SamplePerson();

  public static class SamplePerson {
    public String name = "Taleb";
    public int age = 18;
    public OffsetDateTime birthdate = OffsetDateTime.of(1990, 1, 2, 3, 4, 5, 6, ZoneOffset.UTC);
    public SampleAddress address = new SampleAddress();
  }

  @Test
  void testAll() {
    SamplePerson a = new SamplePerson();
    System.out.println("CSV\n" + Nodes.csv.excluding("address").toString(a));
    System.out.println("CSV with YML\n" + Nodes.csv.toString(a));
    System.out.println("GSON\n" + Nodes.gson.toString(a));
    System.out.println("JSON\n" + Nodes.json.toString(a));
    System.out.println("PROP\n" + Nodes.prop.toString(a));
    System.out.println("XML\n" + Nodes.xml.toString(a));
    System.out.println("YML\n" + Nodes.yml.toString(a));
  }

  @Test
  void testProperties() {
    testBattery(Nodes.prop, ".properties");
  }

  @Test
  void testYml() {
    testBattery(Nodes.yml, ".yml");
  }

  @Test
  void testXml() {
    testBattery(Nodes.xml, ".xml");
  }

  @Test
  void testJson() {
    testBattery(Nodes.json, ".json");
  }

  @Test
  void testGson() {
    testBattery(Nodes.gson, "-gson.json");
  }

  @Test
  void testHocon() {
    testBattery(Nodes.hocon, ".conf");
  }

  @Test
  @Disabled // jxb needs @XmlRootElement
  void testJxb() {
    SamplePerson a = new SamplePerson();
    final Nodes nodes = Nodes.jxb;
    System.out.println(nodes.toString(a));
    assertThat(nodes.toString(a))
      .isEqualToNormalizingNewlines("<SamplePerson>\n" + "  <name>Taleb</name>\n" + "  <age>18</age>\n"
          + "  <birthdate>1990-01-02T03:04:05.000000006Z</birthdate>\n" + "  <address>\n"
          + "    <country>Romania</country>\n"
          + "    <city>Bucharest</city>\n" + "  </address>\n" + "</SamplePerson>\n");
    testDeserialization(nodes, a);
  }

  @Test
  void testCsv() {
    testBattery(Nodes.csv.excluding("address"), ".csv", false);
  }

  @Test
  void testCsvAdvanced() {
    testBattery(Nodes.csv, "-advanced.csv", false);
  }

  @Test
  void testCsvOneLevelObjectAsList() {
    List<SampleAddress> a = Nodes.csv.toList("city,country\nBucharest,Romania", SampleAddress.class);
    assertThat(a.get(0)).usingRecursiveComparison().isEqualTo(new SampleAddress());
  }

  @Test
  void testCsvOneLevelObjectAsListSwappedColumns() {
    List<SampleAddress> a = Nodes.csv.toList("country,city\nRomania,Bucharest", SampleAddress.class);
    assertThat(a.get(0)).usingRecursiveComparison().isEqualTo(new SampleAddress());
  }

  @Test
  void testCsvOneLevelObjectAsListWithTwoObjects() {
    List<SampleAddress> a = Nodes.csv.toList("city,country\nBucharest,Romania\nBucharest2,Romania2",
      SampleAddress.class);
    assertThat(a.get(0)).usingRecursiveComparison().isEqualTo(new SampleAddress());
    assertThat(a.get(1)).usingRecursiveComparison().isEqualTo(new SampleAddress("Romania2", "Bucharest2"));
  }

  @Test
  void testCsvOneLevelObjectAsListWithTwoObjectsGeneric() throws JsonMappingException, JsonProcessingException {
    JsonNode a = Nodes.csv.mapper().readTree("city,country\nBucharest,Romania\nBucharest2,Romania2");
    System.out.println(a);
    assertThat(a.get(0).toString()).isEqualTo("\"city\"");
    assertThat(a.get(1).toString()).isEqualTo("\"country\"");
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

  private void testDeserialization(Nodes nodes, Object o) {
    String expected = nodes.toString(o);
    //System.out.println(expected);
    String actual = nodes.toString(nodes.toObject(expected, o.getClass()));
    assertThat(actual).isEqualTo(expected);
  }

  private String normalize(String content) {
    return content.replaceAll("(\r\n)", "\n");
  }

  private String normalizeAndShow(String content) {
    return content.replaceAll("\r", "").replaceAll("\n", "\\\\n\n");
  }

  private String normalizePreserveLineFeed(String content) {
    return content.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n\n");
  }

  private void testBattery(final Nodes nodes, final String extension) {
    testBattery(nodes, extension, true);
  }

  private void testBattery(final Nodes nodes, final String extension, boolean testDeserialization) {
    System.out.println(nodes.toString(a));
    assertThat(normalize(nodes.toString(a)))
      .isEqualTo(normalize(Locations.classpath("test1/sample" + extension).readContent()));
    assertThat(normalize(nodes.toString(Arrays.asList(a, b))))
      .isEqualTo(normalize(Locations.classpath("test2/sample" + extension).readContent()));
    if (testDeserialization) {
      testDeserialization(nodes, a);
    }
  }

  @Value
  @lombok.NoArgsConstructor(force = true)
  @lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
  @lombok.With
  @lombok.Builder(access = AccessLevel.PRIVATE)
  //@lombok.experimental.FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  //@lombok.Getter(value = AccessLevel.NONE)
  //@lombok.Setter(value = AccessLevel.NONE)
  public static class Point {
    @Builder.Default
    private final int x = 3;
    private final int y;
  }

  @Test
  void testWithers() {
    assertThat(new Point().withY(2).toString()).isEqualTo("NodesTest.Point(x=3, y=2)");
    assertThat(new Point().withX(5).withY(2).toString()).isEqualTo("NodesTest.Point(x=5, y=2)");
    assertThat(new Point().withX(5).toString()).isEqualTo("NodesTest.Point(x=5, y=0)");
  }
}
