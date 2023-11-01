[![Versions](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2Fraisercostin%2Fmaven-repo%2Fmaster%2Forg%2Fraisercostin%2Fjedi-nodes-java%2Fmaven-metadata.xml)](https://github.com/raisercostin/maven-repo/tree/master/org/raisercostin/jedi-nodes-java)


# jedi-nodes-java
Utilities to serialize/deserialize to/from data formats: yaml, json, gson, xml, properties, 
csv, hocon, transposed json, etc

## Features
Dataformat to POJO Mappers
- yaml
- json & gson
- xml
- xml-jxb (POJO + mandatory jxb annotations)
- properties
- csv
- csv advanced (for hierarchies the encapsulated objects are transformed to yaml - or 
other format)
- Use jackson detection & serialization of cycles
  - http://www.cowtowncoder.com/blog/archives/2012/03/entry_466.html
  - https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
- retry if cycles are detected
- max limit of objects reached - https://github.com/abid-khan/depth-wise-json-serializer
- max limits of depth serialization - https://github.com/abid-khan/depth-wise-json-serializer
- http://commons.apache.org/proper/commons-configuration/userguide/quick_start.html 

## TODO
- hocon
- transposed json (arrays of arrays, first row of column names - smaller footprint)
- [ ] add json schema generation
- [ ] add json schema validation
  - https://www.baeldung.com/introduction-to-json-schema-in-java
  - see https://json-schema.org/implementations.html
  - https://github.com/leadpony/justify
  - https://github.com/java-json-tools/json-schema-validator

# Usage


## Code

See test
```
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
  @Test
  void testDeserialization() {
    SamplePerson p1 = new SamplePerson();
    var content = Nodes.json.toString(p1);
    System.out.println(content);
    var p2 = Nodes.json.toObject(content, SamplePerson.class);
    System.out.println(p2);
  }
  ...
}
```

This will print
```
CSV
address,age,birthdate,name
,18,"1990-01-02T03:04:05.000000006Z",Taleb

CSV with YML
address,age,birthdate,name
"---
country: ""Romania""
city: ""Bucharest""
",18,"1990-01-02T03:04:05.000000006Z",Taleb

GSON
{
  "name": "Taleb",
  "age": 18,
  "birthdate": "1990-01-02T03:04:05.000000006Z",
  "address": {
    "country": "Romania",
    "city": "Bucharest"
  }
}
JSON
{
  "name" : "Taleb",
  "age" : 18,
  "birthdate" : "1990-01-02T03:04:05.000000006Z",
  "address" : {
    "country" : "Romania",
    "city" : "Bucharest"
  }
}
PROP
name=Taleb
age=18
birthdate=1990-01-02T03:04:05.000000006Z
address.country=Romania
address.city=Bucharest

XML
<SamplePerson>
  <name>Taleb</name>
  <age>18</age>
  <birthdate>1990-01-02T03:04:05.000000006Z</birthdate>
  <address>
    <country>Romania</country>
    <city>Bucharest</city>
  </address>
</SamplePerson>

YML
---
name: "Taleb"
age: 18
birthdate: "1990-01-02T03:04:05.000000006Z"
address:
  country: "Romania"
  city: "Bucharest"

```

## Maven

Include this pom as parent.

```xml
<dependency>
  <groupId>org.raisercostin</groupId>
  <artifactId>jedi-nodes-java</artifactId>
  <version>0.4-SNAPSHOT</version>
</dependency>
``` 

Browse https://github.com/raisercostin/maven-repo/tree/master/org/raisercostin/jedi-nodes-java

```
<repository>
  <id>raisercostin-github</id>
  <url>https://raw.githubusercontent.com/raisercostin/maven-repo/master/</url>
  <snapshots><enabled>false</enabled></snapshots>
</repository>
```

## Development

- To release 
  - ```
    npm run release-prepare
    npm run release-perform-local -- --releaseVersion 0.86
    ```
