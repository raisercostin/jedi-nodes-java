package org.raisercostin.nodes.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
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
import org.apache.commons.lang3.math.NumberUtils;

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

  public static <T extends ObjectMapper> T configure(T mapper) {
    return configure(mapper, true);
  }

  public static <T extends ObjectMapper> T configure(T mapper, boolean failOnUnknwon, String... excludedFields) {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknwon);
    mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withCreatorVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
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
    // see more in ConfigFeature.class and JsonGenerator.Feature
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new VavrModule());
    // mapper.enable(Feature.IGNORE_UNDEFINED);
    mapper.setSerializationInclusion(Include.NON_NULL);
    configureExclusions(mapper, excludedFields);
    // mapper.setDefaultPrettyPrinter(createCustomPrettyPrinter());
    // mapper.setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
    return mapper;
  }

  //TODO doesn't work for xml
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
    if (excludedFields != null)
      mapper.setFilterProvider(
          new SimpleFilterProvider().addFilter("filter properties by name", SimpleBeanPropertyFilter.serializeAllExcept(excludedFields)));
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
          boundFields = new LinkedHashMap(boundFields) {

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
        return NumberUtils.createNumber(value);
      } catch (NumberFormatException e) {
        throw new JsonSyntaxException(e);
      }
    }
  }
}
