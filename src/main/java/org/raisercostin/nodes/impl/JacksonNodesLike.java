package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import io.vavr.Function1;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.raisercostin.nodes.JacksonNodes;

public interface JacksonNodesLike<SELF extends JacksonNodes, MAPPER extends ObjectMapper, SCHEMA extends FormatSchema> {
  /** In case jackson is used and more flexibility is needed. */
  MAPPER mapper();

  SELF create(MAPPER configure);

  /** In case jackson is used and more flexibility is needed. */
  //<T extends ObjectMapper> T mapper();

  default SELF excluding(String... excludedFields) {
    @SuppressWarnings("unchecked")
    final MAPPER mapper = (MAPPER) mapper().copy();
    JacksonUtils.configureExclusions(mapper, excludedFields);
    return create(mapper);
  }

  @SuppressWarnings("unchecked")
  default SELF parseWithFailOnUnknwon() {
    return create((MAPPER) JacksonUtils.configure(mapper().copy(), true));
  }

  default SELF withMapper(Function1<MAPPER, ObjectMapper> mapperChanger) {
    final MAPPER mapper = (MAPPER) mapperChanger.apply(mapper());
    return create(mapper);
  }

  @SuppressWarnings("unchecked")
  default SELF withCopyMapper() {
    return withMapper(mapper -> (MAPPER) mapper.copy());
  }

  @SuppressWarnings("unchecked")
  default SELF withIgnoreUnknwon() {
    return withMapper(mapper -> (MAPPER) mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
  }

  default SELF withPrefix(String rootName) {
    throw new RuntimeException("Not implemented yet!!!");
  }

  @SuppressWarnings("unchecked")
  default SELF withSchema(SCHEMA formatSchema) {
    return (SELF) this;
  }

  default SELF withIdenter(String indent, String eol) {
    return withIdenter(new DefaultIndenter(indent, eol));
  }

  default SELF withDefaultPrettyPrinter(Function1<DefaultPrettyPrinter, DefaultPrettyPrinter> printerChange) {
    return withPrettyPrinter(
      printerChange.apply((DefaultPrettyPrinter) mapper().getSerializationConfig().getDefaultPrettyPrinter()));
  }

  default SELF withPrettyPrinter(Function1<PrettyPrinter, PrettyPrinter> printerChange) {
    return withPrettyPrinter(
      printerChange.apply(mapper().getSerializationConfig().getDefaultPrettyPrinter()));
  }

  default SELF withIdenter(@NonNull Indenter indenter) {
    DefaultPrettyPrinter defaultPrettyPrinter = (DefaultPrettyPrinter) mapper().getSerializationConfig()
      .getDefaultPrettyPrinter();
    DefaultPrettyPrinter printer = new DefaultPrettyPrinter(defaultPrettyPrinter);
    printer.indentObjectsWith(indenter);
    printer.indentArraysWith(indenter);
    //return withPrettyPrinter(printer.withoutSpacesInObjectEntries());
    //return withPrettyPrinter(printer.withSpacesInObjectEntries());
    return withPrettyPrinter(printer);
  }

  /**Create new mapper.*/
  default SELF withPrettyPrinter(PrettyPrinter prettyPrinter) {
    return withMapper(mapper -> {
      mapper = (MAPPER) mapper.copy();
      mapper.setDefaultPrettyPrinter(prettyPrinter);
      return mapper;
    });
  }

  /**Change mapper inplace.*/
  default SELF setCustomPrinter(PrettyPrinter prettyPrinter) {
    return withMapper(mapper -> {
      mapper.setDefaultPrettyPrinter(prettyPrinter);
      return mapper;
    });
  }

}

class PrefixStrategy extends PropertyNamingStrategyBase {
  private String prefix;

  public PrefixStrategy(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public String translate(String input) {
    return prefix + input;
  }
}
