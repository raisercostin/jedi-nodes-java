package org.raisercostin.nodes.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import org.raisercostin.nodes.ExceptionUtils;
import org.raisercostin.nodes.Nodes;

public class CsvUtils2 implements JacksonNodes {
  private final CsvMapper mapper;

  public CsvUtils2() {
    this(JacksonUtils.configure(new CsvMapper()));
  }

  public CsvUtils2(CsvMapper mapper) {
    this.mapper = mapper;
    SimpleModule module = new SimpleModule();
    module.setSerializerModifier(new MyBeanSerializerModifier());
    mapper.registerModule(module);
  }

  private static class MyBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public JsonSerializer<?> modifySerializer(final SerializationConfig serializationConfig, final BeanDescription beanDescription,
        final JsonSerializer<?> jsonSerializer) {
      return new ModifyingSerializer((JsonSerializer<Object>) jsonSerializer);
    }
  }

  private static class ModifyingSerializer extends JsonSerializer<Object> {
    private final JsonSerializer<Object> serializer;

    public ModifyingSerializer(final JsonSerializer<Object> jsonSerializer) {
      this.serializer = jsonSerializer;
    }

    @Override
    public void serialize(final Object o, final JsonGenerator gen, final SerializerProvider ser) throws IOException {
      // CsvGenerator csvGen = (CsvGenerator) gen;
      if (isRow(gen)) {
        // serializerProvider.findValueSerializer(String.class).serialize(Nodes.json.toString(o), jsonGenerator,
        // serializerProvider);
        // System.out.println("ignore");
        // serializerProvider.findValueSerializer(String.class).serialize("ignore", jsonGenerator, serializerProvider);
        // JsonStreamContext sc = gen.getOutputContext();
        // System.out.println(sc + " => " + o);
        // CsvSchema schema = (CsvSchema) csvGen.getSchema();
        //
        // switch (computeType(gen)) {
        // case ARRAY:
        // break;
        // case BOOLEAN:
        // break;
        // case NUMBER:
        // gen.writeNumber(Nodes.json.toString(o));
        // break;
        // case NUMBER_OR_STRING:
        // gen.writeString(Nodes.json.toString(o));
        // break;
        // case STRING:
        // gen.writeString(Nodes.json.toString(o));
        // break;
        // case STRING_OR_LITERAL:
        // gen.writeString(Nodes.json.toString(o));
        // break;
        // default:
        // break;
        // }
        if (serializer instanceof BeanSerializer)
          //isComplex(o, ser, gen)
          // gen.writeString(stripQuotes(Nodes.yml.toString(o)));
          gen.writeString(Nodes.yml.toString(o));
        else
          serializer.serialize(o, gen, ser);
      } else
        serializer.serialize(o, gen, ser);
    }

    // private boolean isComplex(Object o, SerializerProvider ser, JsonGenerator gen) throws JsonMappingException {
    // return serializer instanceof BeanSerializer;
    // JsonStreamContext _writeContext = gen.getOutputContext();
    // return _writeContext.inObject();
    // JsonSerializer<Object> ser1 = ser.(o.getClass());
    // System.out.println(ser1);
    // return false;
    // if(o instanceof String)
    // return false;
    // if(o instanceof Number)
    // return false;
    // return true;
    // }
    //
    // private String stripQuotes(String content) {
    // if(content.startsWith("\"")&&content.endsWith("\""))
    // return content.substring(1,content.length()-1);
    // else
    // return content;
    // }
    //
    // private ColumnType computeType(JsonGenerator gen) {
    // throw new RuntimeException("Not implemented yet!!!");
    // }

    private boolean isRow(JsonGenerator gen) {
      return gen.getOutputContext().getParent() != null;
    }
    //
    // private int computeDepth(JsonGenerator jgen) {
    // JsonStreamContext sc = jgen.getOutputContext();
    // int depth = -1;
    // while (sc != null) {
    // sc = sc.getParent();
    // depth++;
    // }
    // return depth;
    // }
  }

  @SuppressWarnings("unchecked")
  @Override
  public CsvMapper mapper() {
    return mapper;
  }

  @Override
  public <T> String toString(T value) {
    return ExceptionUtils.tryWithSuppressed(() -> {
      Object oneValue;
      if (value instanceof Iterable) {
        oneValue = ((Iterable) value).iterator().next();
      } else
        oneValue = value;
      CsvSchema schema = mapper.schemaFor(oneValue.getClass()).withHeader().withComments();
      return mapper.writer(schema).writeValueAsString(value);
    }, "");
  }

  @Override
  public CsvUtils2 newNodes(ObjectMapper mapper) {
    return new CsvUtils2((CsvMapper) mapper);
  }
}
