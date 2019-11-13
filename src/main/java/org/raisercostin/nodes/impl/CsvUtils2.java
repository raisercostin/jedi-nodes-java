package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;
import org.raisercostin.nodes.Nodes;

public class CsvUtils2 implements JacksonNodes {
  private final CsvMapper mapper;

  public CsvUtils2() {
    this(JacksonUtils.configure(new CsvMapper()));
    mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
  }

  public CsvUtils2(CsvMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public CsvMapper mapper() {
    return mapper;
  }

  @SneakyThrows
  @Override
  public <T> String toString(T value) {
    Object oneValue;
    if (value instanceof Iterable) {
      oneValue = ((Iterable) value).iterator().next();
    } else
      oneValue = value;
    CsvSchema schema = mapper.schemaFor(oneValue.getClass());
    return mapper.writer(schema).writeValueAsString(value);
  }

  @SneakyThrows
  @Override
  public <T> T toObject(String content, Class<T> clazz) {
    return mapper.readValue(content, clazz);
  }

  @Override
  public CsvUtils2 newNodes(ObjectMapper mapper) {
    return new CsvUtils2((CsvMapper) mapper);
  }
}
