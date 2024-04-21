package org.raisercostin.nodes.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.vavr.Function1;
import io.vavr.jackson.datatype.VavrModule;

public class JacksonUtils {
  public static ObjectMapper createObjectMapper() {
    return createObjectMapper(false);
  }

  public static ObjectMapper createObjectMapper(JsonFactory jsonFactory) {
    ObjectMapper mapper = new ObjectMapper(jsonFactory);
    configure(mapper, false);
    return mapper;
  }

  public static ObjectMapper createObjectMapper(boolean failOnUnknwon) {
    ObjectMapper mapper = new ObjectMapper();
    configure(mapper, failOnUnknwon);
    return mapper;
  }

  /** See more configuration in ConfigFeature, JsonGenerator.Feature and FormatFeature. */
  public static <T extends ObjectMapper> T configure(T mapper) {
    return configure(mapper, true);
  }

  /** See more configuration in ConfigFeature, JsonGenerator.Feature and FormatFeature. */
  public static <T extends ObjectMapper> T configure(T mapper, boolean failOnUnknwon, String... excludedFields) {
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
    mapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);

    //Will not work if the key is not comparable
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
    // needed for jackson serialized exception cause `Direct self-reference leading to cycle` for (through reference
    // chain: java.lang.IllegalArgumentException["cause"]->java.lang.IllegalArgumentException["cause"] )
    mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, true);
    // mapper.mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
    // mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    // mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    // A bug in jackson 2.10.0 prevents deserializing the catalog with this option active. Better to be switched on when is necessary
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true);

    // mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new VavrModule());
    mapper.registerModule(new ThrowablesModule());
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new ThrowablesModule());
    //TODO configured by default
    //mapper.registerModule(new JsonComponentModule());
    //mapper.registerModule(new JacksonXmlModule());
    //mapper.registerModule(new JaxbAnnotationModule());

    // mapper.enable(Feature.IGNORE_UNDEFINED);
    mapper.setSerializationInclusion(Include.NON_NULL);
    configureExclusions(mapper, excludedFields);
    if (mapper instanceof XmlMapper) {
      ((XmlMapper) mapper).setDefaultUseWrapper(false);
    }
    if (mapper instanceof JsonMapper) {
      DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
      DefaultPrettyPrinter defaultPrettyPrinter = (DefaultPrettyPrinter) mapper.getSerializationConfig()
        .getDefaultPrettyPrinter();
      DefaultPrettyPrinter printer = new DefaultPrettyPrinter(defaultPrettyPrinter);
      printer.indentObjectsWith(indenter);
      printer.indentArraysWith(indenter);
      mapper.setDefaultPrettyPrinter(printer);
    }

    // mapper.setDefaultPrettyPrinter(createCustomPrettyPrinter());
    // mapper.setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
    //configure end of lines platform independent \n for all: xml, json, yml - see https://github.com/FasterXML/jackson-databind/issues/585
    return mapper;
  }

  /** This can be used to serialize exceptions but most probably not to deserialize them back.*/
  public static class ThrowablesModule extends SimpleModule {
    private static final long serialVersionUID = -2687534903247863765L;

    //@JsonIgnoreProperties({ "$id" })
    @JsonDeserialize(using = ThrowableMixIn.ThrowableDeserializer.class)
    @JsonSerialize(using = ThrowableMixIn.ThrowableSerializer.class)
    public static abstract class ThrowableMixIn {
      @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "$id2")
      private Throwable cause;

      static class ThrowableDeserializer extends JsonDeserializer<Throwable> {
        @Override
        public Throwable deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
          if (p == null) {
            return null;
          }
          return new Throwable(p.readValueAs(String.class));
        }
      }

      static class ThrowableSerializer extends JsonSerializer<Throwable> {
        @Override
        public void serialize(Throwable value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
          serializers.findValueSerializer(String.class).serialize(toSerializableString(value), gen, serializers);
        }

        private String toSerializableString(Throwable throwable) {
          final StringWriter sw = new StringWriter();
          final PrintWriter pw = new PrintWriter(sw, true);
          throwable.printStackTrace(pw);
          return sw.getBuffer().toString();
        }
      }

      //
      //    public abstract class StackTraceElementMixin {
      //      @JsonProperty("className")
      //      private String declaringClass;
      //    }
      //
      //    public ThrowablesModule() {
      //      super("throwables");
      //    }
    }

    @Override
    public void setupModule(SetupContext context) {
      context.setMixInAnnotations(Throwable.class, ThrowableMixIn.class);
      //context.setMixInAnnotations(StackTraceElement.class, StackTraceElementMixin.class);
    }
  }

  //
  //  // TODO doesn't work for xml
  //  private static PrettyPrinter createCustomPrettyPrinter() {
  //    // Setup a pretty printer with an indenter (indenter has 4 spaces in this case)
  //    // DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("", DefaultIndenter.SYS_LF);
  //    DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("  ", "\n");
  //    DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
  //    printer.indentObjectsWith(indenter);
  //    printer.indentArraysWith(indenter);
  //    return printer.withoutSpacesInObjectEntries();
  //  }
  private static final String EXCLUSION_FILTER_ID = "exclusionFilter";

  @JsonFilter(EXCLUSION_FILTER_ID)
  private class FieldExclusionFilterMixIn {
  }

  public static <T extends ObjectMapper> void configureExclusions(T mapper, String... excludedFields) {
    configureRedactingAndHiding(mapper,
      old -> CustomFieldRedactingFilter.addExclusions((CustomFieldRedactingFilter) old, excludedFields));
  }

  public static <MAPPER extends ObjectMapper> void configureRedacting(String fieldPattern, String value, MAPPER mapper,
      String... redactingFields) {
    configureRedactingAndHiding(mapper,
      old -> CustomFieldRedactingFilter.addRedacting((CustomFieldRedactingFilter) old, fieldPattern, value,
        redactingFields));
  }

  public static <T extends ObjectMapper> void configureExclusionsAndRemoveRedacting(T mapper,
      String... excludedFields) {
    mapper = (T) mapper.addMixIn(Object.class, FieldExclusionFilterMixIn.class);
    if (excludedFields != null) {
      addFilter(mapper, EXCLUSION_FILTER_ID, _old -> SimpleBeanPropertyFilter.serializeAllExcept(excludedFields));
    }
  }

  public static final String ALL_OBJECTS_FILTER_ID = "allObjectsFilter";

  @JsonFilter(ALL_OBJECTS_FILTER_ID)
  static class FieldRedactingFilterMixIn {
  }

  @SuppressWarnings("unchecked")
  public static <T extends ObjectMapper, F extends PropertyFilter> T configureRedactingAndHiding(T mapper,
      Function1<F, F> transform) {
    mapper = (T) mapper.addMixIn(Object.class, FieldRedactingFilterMixIn.class);
    addFilter(mapper, ALL_OBJECTS_FILTER_ID, transform);
    return mapper;
  }

  public static class SimpleFilterProvider2 extends SimpleFilterProvider {
    public SimpleFilterProvider2() {
    }

    public SimpleFilterProvider2(Map<String, PropertyFilter> map) {
      super(map);
    }

    SimpleFilterProvider2 copy() {
      return new SimpleFilterProvider2(new HashMap<>(super._filtersById));
    }
  }

  private static <T extends ObjectMapper, F extends PropertyFilter> void addFilter(T mapper, String filterId,
      Function1<F, F> transform) {
    SimpleFilterProvider2 filter = (SimpleFilterProvider2) mapper.getSerializationConfig().getFilterProvider();
    if (filter == null) {
      filter = new SimpleFilterProvider2();
    }
    boolean willFail = filter.willFailOnUnknownId();
    filter.setFailOnUnknownId(false);
    F old = (F) filter.findPropertyFilter(filterId, null);
    filter.setFailOnUnknownId(willFail);
    F newFilter = transform.apply(old);
    if (newFilter != null) {
      filter.addFilter(filterId, newFilter);
    }
    mapper.setFilterProvider(filter);
  }

  //Custom property filter
  public static class CustomFieldRedactingFilter extends SimpleBeanPropertyFilter {

    public static CustomFieldRedactingFilter addRedacting(CustomFieldRedactingFilter old, String fieldPattern,
        String value, String[] fieldsToRedact) {
      if (old == null) {
        return new CustomFieldRedactingFilter(fieldPattern, value, toSet(fieldsToRedact), new HashSet<>());
      }
      return new CustomFieldRedactingFilter(fieldPattern, value, toSet(fieldsToRedact), old.fieldsToHide);
    }

    public static CustomFieldRedactingFilter addExclusions(CustomFieldRedactingFilter old, String[] fieldsToHide) {
      if (old == null) {
        return new CustomFieldRedactingFilter(null, null, new HashSet<>(), toSet(fieldsToHide));
      }
      return new CustomFieldRedactingFilter(old.fieldPattern, old.value, old.fieldsToRedact, toSet(fieldsToHide));
    }

    private static Set<String> toSet(String[] fields) {
      return fields == null ? Set.of() : new HashSet<>(Arrays.asList(fields));
    }

    public String fieldPattern;
    public String value;
    public Set<String> fieldsToRedact;
    public Set<String> fieldsToHide;

    public CustomFieldRedactingFilter(String fieldPattern, String value, Set<String> fieldsToRedact,
        Set<String> fieldsToHide)
    {
      this.fieldPattern = fieldPattern;
      this.value = value;
      this.fieldsToRedact = fieldsToRedact;
      this.fieldsToHide = fieldsToHide;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
        throws Exception {
      if (!fieldsToHide.contains(writer.getName())) {
        if (fieldsToRedact != null && fieldsToRedact.contains(writer.getName())) {
          jgen.writeFieldName(fieldPattern.formatted(writer.getName()));
          jgen.writeString(value);
        } else {
          super.serializeAsField(pojo, jgen, provider, writer);
        }
      }
    }
  }

  private static class ValidatorAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      // If the type adapter is a reflective type adapter, we want to modify the implementation using reflection. The
      // trick is to replace the Map object used to lookup the property name. Instead of returning null if the
      // property is not found, we throw a Json exception to terminate the deserialization.
      TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

      // Check if the type adapter is a reflective, cause this solution only work for reflection.
      if (delegate instanceof ReflectiveTypeAdapterFactory.Adapter) {

        try {
          // Get reference to the existing boundFields.
          Field f = delegate.getClass().getDeclaredField("boundFields");
          f.setAccessible(true);
          Map boundFields = (Map) f.get(delegate);

          // Then replace it with our implementation throwing exception if the value is null.
          boundFields = new LinkedHashMap(boundFields)
            {

              @Override
              public Object get(Object key) {

                Object value = super.get(key);
                if (value == null) {
                  throw new JsonParseException("invalid property name: " + key);
                }
                return value;

              }

            };
          // Finally, push our custom map back using reflection.
          f.set(delegate, boundFields);

        } catch (Exception e) {
          // Should never happen if the implementation doesn't change.
          throw new IllegalStateException(e);
        }

      }
      return delegate;
    }
  }

  private static class EmptyStringToNumberTypeAdapter extends TypeAdapter<Number> {
    @Override
    public void write(JsonWriter jsonWriter, Number number) throws IOException {
      if (number == null) {
        jsonWriter.nullValue();
        return;
      }
      jsonWriter.value(number);
    }

    @Override
    public Number read(JsonReader jsonReader) throws IOException {
      if (jsonReader.peek() == JsonToken.NULL) {
        jsonReader.nextNull();
        return null;
      }

      try {
        String value = jsonReader.nextString();
        if ("".equals(value)) {
          return 0;
        }
        // commons-lang3:3.5
        // return org.apache.commons.lang3.math.NumberUtils.createNumber(value);
        return new BigDecimal(value);
      } catch (NumberFormatException e) {
        throw new JsonSyntaxException(e);
      }
    }
  }
}
