[![Download](https://api.bintray.com/packages/raisercostin/maven/jedi-nodes-java/images/download.svg)](https://bintray.com/raisercostin/maven/jedi-nodes-java/_latestVersion)

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

Repository at https://dl.bintray.com/raisercostin/maven/org/raisercostin/jedi-nodes-java

```
<repository>
  <id>raisercostin-bintray</id>
  <url>https://dl.bintray.com/raisercostin/maven</url>
  <releases><enabled>true</enabled></releases>
  <snapshots><enabled>false</enabled></snapshots>
</repository>
```

# Development
- To release
  `mvn release:prepare release:perform -DskipTests=true -Prelease -Darguments="-DskipTests=true -Prelease"` 
- To update versions for a profile
  `mvn versions:update-properties -Pjunit5`
