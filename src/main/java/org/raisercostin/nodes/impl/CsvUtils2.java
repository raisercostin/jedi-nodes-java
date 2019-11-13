package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.raisercostin.nodes.ExceptionUtils;

public class CsvUtils2 implements JacksonNodes {
  private final CsvMapper mapper;

  public CsvUtils2() {
    this(JacksonUtils.configure(new CsvMapper()));
  }

  public CsvUtils2(CsvMapper mapper) {
    this.mapper = mapper;
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
