[![Download](https://api.bintray.com/packages/raisercostin/maven/jedi-nodes-java/images/download.svg)](https://bintray.com/raisercostin/maven/jedi-nodes-java/_latestVersion)

# jedi-nodes-java
Generic node containers serializable/deserializable with jackson and other libraries.

Conversions between
- yaml
- json
- xml
- properties
- csv
- hocon
- ...

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
    System.out.println(Nodes.csv.excluding("address").toString(a));
    System.out.println(Nodes.gson.toString(a));
    System.out.println(Nodes.json.toString(a));
    System.out.println(Nodes.prop.toString(a));
    System.out.println(Nodes.xml.toString(a));
    System.out.println(Nodes.yml.toString(a));
  }
  ...
}
```

This will print
```
address,age,birthdate,name
,18,"1990-01-02T03:04:05.000000006Z",Taleb

{
  "name": "Taleb",
  "age": 18,
  "birthdate": "1990-01-02T03:04:05.000000006Z",
  "address": {
    "country": "Romania",
    "city": "Bucharest"
  }
}
{
  "name" : "Taleb",
  "age" : 18,
  "birthdate" : "1990-01-02T03:04:05.000000006Z",
  "address" : {
    "country" : "Romania",
    "city" : "Bucharest"
  }
}
name=Taleb
age=18
birthdate=1990-01-02T03:04:05.000000006Z
address.country=Romania
address.city=Bucharest

<SamplePerson>
  <name>Taleb</name>
  <age>18</age>
  <birthdate>1990-01-02T03:04:05.000000006Z</birthdate>
  <address>
    <country>Romania</country>
    <city>Bucharest</city>
  </address>
</SamplePerson>

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
<project>
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>org.raisercostin</groupId>
    <artifactId>maven-defaults-raisercostin</artifactId>
    <version>1.0</version>
  </parent>
  ...
</project>
``` 

No need to include a repository as is available at https://jcenter.bintray.com/org/raisercostin/maven-defaults-raisercostin/

# Development
- To release
  `mvn release:prepare release:perform -DskipTests=true -Prelease -Darguments="-DskipTests=true -Prelease"` 
- To update versions for a profile
  `mvn versions:update-properties -Pjunit5`
