package org.raisercostin.nodes.jackson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.vavr.API;
import io.vavr.collection.Seq;
import org.raisercostin.nodes.impl.ExceptionUtils;

public class MultiDateDeserializer extends StdDeserializer<LocalDate> {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MultiDateDeserializer.class);

  private static final long serialVersionUID = 1L;

  private static final Seq<DateTimeFormatter> DATE_FORMATS = API.Seq("dd-MMM-yyyy", "dd-MMM-yy")
    .map(x -> new DateTimeFormatterBuilder().parseCaseInsensitive()
      .appendPattern(x)
      .toFormatter(Locale.ENGLISH));

  public MultiDateDeserializer() {
    this(null);
  }

  public MultiDateDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) {
    try {
      JsonNode node = jp.getCodec().readTree(jp);
      final String date = node.textValue();
      //firstSuccess=DATE_FORMATS.toStream().map(x -> Try.of(() -> LocalDate.parse(date, x))).find(x->x.isSuccess());
      Throwable sample = null;
      for (DateTimeFormatter formatter : DATE_FORMATS) {
        try {
          return LocalDate.parse(date, formatter);
        } catch (java.time.format.DateTimeParseException e) {
          sample = e;
          log.debug("Cannot parse with {}", formatter, e);
        }
      }
      throw new JsonParseException(jp, "Unparseable date: \"" + date + "\". Supported formats: " + DATE_FORMATS,
        sample);
    } catch (IOException e1) {
      throw ExceptionUtils.nowrap(e1);
    }
  }
}