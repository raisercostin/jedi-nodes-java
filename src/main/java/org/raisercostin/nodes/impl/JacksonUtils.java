package org.raisercostin.nodes.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
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

  // mixin for filter of fields
  @JsonFilter("filter properties by name")
  private class PropertyFilterMixIn {
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

    //Will not work if the key is not comparable
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
    // needed for jackson serialized exception cause `Direct self-reference leading to cycle` for (through reference
    // chain: java.lang.IllegalArgumentException["cause"]->java.lang.IllegalArgumentException["cause"] )
    mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, true);
    // mapper.mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
    // mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    // mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true);

    // mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new VavrModule());
    mapper.registerModule(new ThrowablesModule());
    // mapper.enable(Feature.IGNORE_UNDEFINED);
    mapper.setSerializationInclusion(Include.NON_NULL);
    configureExclusions(mapper, excludedFields);
    // mapper.setDefaultPrettyPrinter(createCustomPrettyPrinter());
    // mapper.setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
    //configure end of lines platform independent \n for all: xml, json, yml - see https://github.com/FasterXML/jackson-databind/issues/585
    return mapper;
  }

  /** This can be used to serialize exceptions but most probably not to deserialize them back.*/
  public static class ThrowablesModule extends SimpleModule {
    private static final long serialVersionUID = -2687534903247863765L;

    @JsonIgnoreProperties({ "$id" })
    public abstract class ThrowableMixIn {
      @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "$id")
      private Throwable cause;
    }

    public ThrowablesModule() {
      super("throwables");
    }

    @Override
    public void setupModule(SetupContext context) {
      context.setMixInAnnotations(Throwable.class, ThrowableMixIn.class);
    }
  }

  // TODO doesn't work for xml
  private static PrettyPrinter createCustomPrettyPrinter() {
    // Setup a pretty printer with an indenter (indenter has 4 spaces in this case)
    // DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("", DefaultIndenter.SYS_LF);
    DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("  ", "\n");
    DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
    printer.indentObjectsWith(indenter);
    printer.indentArraysWith(indenter);
    return printer.withoutSpacesInObjectEntries();
  }

  public static <T extends ObjectMapper> void configureExclusions(T mapper, String... excludedFields) {
    mapper.addMixIn(Object.class, PropertyFilterMixIn.class);
    if (excludedFields != null) {
      mapper.setFilterProvider(
        new SimpleFilterProvider().addFilter("filter properties by name",
          SimpleBeanPropertyFilter.serializeAllExcept(excludedFields)));
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
