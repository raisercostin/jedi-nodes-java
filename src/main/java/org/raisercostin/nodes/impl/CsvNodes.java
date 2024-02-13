package org.raisercostin.nodes.impl;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Column;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.raisercostin.nodes.JacksonNodes;
import org.raisercostin.nodes.Nodes;

public class CsvNodes implements JacksonNodes, JacksonNodesLike<CsvNodes, CsvMapper, CsvSchema> {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CsvNodes.class);

  private final CsvMapper mapper;

  public CsvNodes() {
    this(JacksonUtils.configure(new CsvMapper()));
  }

  public CsvNodes(CsvMapper mapper) {
    this.mapper = mapper;
    // mapper.configure(Feature.WRAP_AS_ARRAY)
    SimpleModule module = new SimpleModule("CsvAdvanced");
    module.setSerializerModifier(new CsvAdvancedBeanSerializerModifier());
    // module.setDeserializerModifier(new CsvAdvancedBeanDeserializerModifier());
    mapper.registerModule(module);
  }

  private static class CsvAdvancedBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public JsonSerializer<?> modifySerializer(final SerializationConfig serializationConfig,
        final BeanDescription beanDescription,
        final JsonSerializer<?> jsonSerializer) {
      return new CsvAdvancedSerializer((JsonSerializer<Object>) jsonSerializer);
    }

    private static class CsvAdvancedSerializer extends JsonSerializer<Object> {
      private final JsonSerializer<Object> serializer;

      public CsvAdvancedSerializer(final JsonSerializer<Object> jsonSerializer) {
        this.serializer = jsonSerializer;
      }

      @Override
      public void serialize(final Object o, final JsonGenerator gen, final SerializerProvider ser) throws IOException {
        if (isRow(gen)) {
          if (serializer instanceof BeanSerializer) {
            gen.writeString(Nodes.yml.toString(o));
          } else {
            serializer.serialize(o, gen, ser);
          }
        } else {
          serializer.serialize(o, gen, ser);
        }
      }

      private boolean isRow(JsonGenerator gen) {
        return gen.getOutputContext().getParent() != null;
      }
    }
  }

  private static class CsvAdvancedBeanDeserializerModifier extends BeanDeserializerModifier {
    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
        JsonDeserializer<?> jsonDeserializer) {
      return new CsvAdvancedDeserializer((JsonDeserializer<Object>) jsonDeserializer);
    }

    private static class CsvAdvancedDeserializer extends // JsonDeserializer<Object>
        StdDeserializer<Object>
    // implements ResolvableDeserializer
    {
      private final JsonDeserializer<Object> deserializer;

      public CsvAdvancedDeserializer(final JsonDeserializer<Object> jsonDeserializer) {
        super(Object.class);
        this.deserializer = jsonDeserializer;
      }

      @Override
      public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        return deserializer.deserialize(p, ctxt, intoValue);
      }

      @Override
      public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // if (isRow(p))
        // if (deserializer instanceof BeanDeserializer)
        // // return Nodes.yml.toObject(p.readValueAs(String.class),deserializer.ctxt.get);
        // // System.out.println("deserializing "+p.readValueAs(String.class)+" to "+ctxt);
        // return p.readValueAs(String.class);
        log.debug("deserialize {} : {}", p.currentToken(), p.currentName());
        return deserializer.deserialize(p, ctxt);
      }

      // for some reason you have to implement ResolvableDeserializer when modifying BeanDeserializer
      // otherwise deserializing throws JsonMappingException??
      // @Override
      // public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      // ((ResolvableDeserializer) deserializer).resolve(ctxt);
      // }

      //
      // @Override
      // public void serialize(final Object o, final JsonGenerator gen, final SerializerProvider ser) throws IOException
      // {
      // if (isRow(gen)) {
      // if (serializer instanceof BeanSerializer)
      // gen.writeString(Nodes.yml.toString(o));
      // else
      // serializer.serialize(o, gen, ser);
      // } else
      // serializer.serialize(o, gen, ser);
      // }
      //
      private boolean isRow(JsonParser p) {
        return p.getParsingContext().getParent() != null;
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public CsvMapper mapper() {
    return mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> String toString(T value) {
    mapper.setSerializationInclusion(Include.NON_NULL);
    return ExceptionUtils.tryWithSuppressed(() -> {
      if (value instanceof Iterable) {
        java.util.Iterator<T> iterator = ((Iterable<T>) value).iterator();
        if (!iterator.hasNext()) {
          return "";
        }
        T oneValue = iterator.next();
        final StringWriter out = new StringWriter();
        objectWriter(oneValue).writeValues(out).writeAll((Iterable<T>) value).flush();
        return out.toString();
      } else {
        T oneValue = value;
        return objectWriter(oneValue).writeValueAsString(value);
      }
    }, "");
  }

  @Override
  public <T> T toObject(String content, Class<T> clazz) {
    return ExceptionUtils.tryWithSuppressed(() -> {
      T value = objectReader(clazz).readValue(content);
      return value;
    }, "Cannot deserialize [%s] to [%s].", content, clazz);
  }

  @Override
  public <T> MappingIterator<T> toMappingIterator(String content, Class<T> clazz) {
    try {
      return objectReader(clazz).readValues(content);
    } catch (IOException e) {
      throw ExceptionUtils.nowrap(e);
    }
  }

  private ObjectWriter objectWriter(Object element) {
    Class<?> clazz = element.getClass();
    if (Map.class.isInstance(element)) {
      //each element is a map
      CsvSchema schema = csvSchemaFromKeys(((Map<Object, Object>) element).keySet().toList());
      return mapper.writer(schema);
    } else if (java.util.Map.class.isInstance(element)) {
      //each element is a map
      CsvSchema schema = csvSchemaFromKeys(
        Iterator.ofAll(((java.util.Map<Object, Object>) element).keySet()).toList());
      return mapper.writer(schema);
    }
    CsvSchema schema = csvSchema(clazz);
    return mapper.writer(schema);
  }

  private CsvSchema csvSchemaFromKeys(List<Object> keySet) {
    CsvSchema.Builder builder = CsvSchema.builder();
    builder.addColumns(keySet.sorted().zipWithIndex().map(c -> new Column(c._2, c._1.toString())));
    CsvSchema schema = builder.build().withHeader().withComments().withColumnReordering(true);
    //.withNullValue("-");
    return schema;
  }

  private ObjectReader objectReader(Class<?> clazz) {
    //    CsvSchema schema = CsvSchema.emptySchema() //
    //        .withHeader();
    //      with(schema).
    CsvSchema schema = csvSchema(clazz);
    return mapper.reader(schema).forType(clazz);
  }

  private CsvSchema csvSchema(Class<?> clazz) {
    CsvSchema schema = mapper.schemaFor(clazz).withHeader().withComments().withColumnReordering(true);
    //.withNullValue("-");
    return schema;
  }

  @Override
  public CsvNodes create(CsvMapper mapper) {
    return new CsvNodes(mapper);
  }

  @SuppressWarnings("unchecked")
  @Override
  public CsvNodes createJacksonNodes(ObjectMapper mapper) {
    return create((CsvMapper) mapper);
  }
}
