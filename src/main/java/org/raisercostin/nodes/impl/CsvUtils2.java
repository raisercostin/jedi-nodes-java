package org.raisercostin.nodes.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
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
      if (isRow(gen)) {
        if (serializer instanceof BeanSerializer)
          gen.writeString(Nodes.yml.toString(o));
        else
          serializer.serialize(o, gen, ser);
      } else
        serializer.serialize(o, gen, ser);
    }

    private boolean isRow(JsonGenerator gen) {
      return gen.getOutputContext().getParent() != null;
    }
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
