package org.raisercostin.nodes.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import io.vavr.jackson.datatype.VavrModule;
import org.raisercostin.nodes.JacksonNodes;

public class XmlJxbThenJacksonNodes
    implements JacksonNodes, JacksonNodesLike<XmlJxbThenJacksonNodes, XmlMapper, FormatSchema> {
  private final XmlMapper mapper;

  public XmlJxbThenJacksonNodes() {
    //this(JacksonUtils.configure(new XmlMapper(), true));
    this(configure2(new XmlMapper(), true));
  }

  public XmlJxbThenJacksonNodes(XmlMapper mapper) {
    this.mapper = mapper;
    configureJaxbObjectMapper(mapper);
  }

  /** See more configuration in ConfigFeature, JsonGenerator.Feature and FormatFeature. */
  // copied from JacksonUtils.configure
  public static <T extends ObjectMapper> T configure2(T mapper, boolean failOnUnknwon, String... excludedFields) {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknwon);
    mapper.setVisibility(mapper.getSerializationConfig()
      .getDefaultVisibilityChecker()
      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
      .withCreatorVisibility(JsonAutoDetect.Visibility.ANY)
      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);
    mapper.configure(SerializationFeature.WRAP_EXCEPTIONS, true);
    // mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    //mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new VavrModule());
    // mapper.enable(Feature.IGNORE_UNDEFINED);
    mapper.setSerializationInclusion(Include.NON_NULL);
    // configureExclusions(mapper, excludedFields);
    // mapper.setDefaultPrettyPrinter(createCustomPrettyPrinter());
    // mapper.setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
    return mapper;
  }

  public static void configureJaxbObjectMapper(XmlMapper mapper) {
    final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
      .appendPattern("dd-MMM-yy")
      .toFormatter(Locale.ENGLISH);
    LocalDateDeserializer dateTimeDeserializer = new LocalDateDeserializer(formatter)
    //    {
    //      @Override
    //      public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    //        throw new RuntimeException("Not implemented yet!!!");
    //      }
    //
    //      @Override
    //      protected LocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
    //        throw new RuntimeException("Not implemented yet!!!");
    //      }
    //    };
    ;
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
    LocalDateSerializer dateTimeSerializer = new LocalDateSerializer(formatter);
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(LocalDate.class, dateTimeDeserializer);
    javaTimeModule.addSerializer(LocalDate.class, dateTimeSerializer);
    mapper.registerModule(javaTimeModule);
    // mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false);
    // // make deserializer use JAXB annotations (only)
    // mapper.getDeserializationConfig().with(introspector);
    // // make serializer use JAXB annotations (only)
    // mapper.getSerializationConfig().with(introspector);
    // mapper.setAnnotationIntrospector(introspector);

    // AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
    final TypeFactory typeFactory = TypeFactory.defaultInstance();
    final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(typeFactory);
    // if ONLY using JAXB annotations:
    // mapper.setAnnotationIntrospector(introspector);
    // if using BOTH JAXB annotations AND Jackson annotations:
    AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
    mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(introspector, secondary));
    // mapper.registerModule(new JaxbAnnotationModule());
  }

  @SuppressWarnings("unchecked")
  @Override
  public XmlMapper mapper() {
    return mapper;
  }

  @Override
  public XmlJxbThenJacksonNodes newNodes(XmlMapper configure) {
    return new XmlJxbThenJacksonNodes(configure);
  }
}
