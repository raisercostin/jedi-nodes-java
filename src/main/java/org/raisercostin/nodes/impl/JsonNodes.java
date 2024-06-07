package org.raisercostin.nodes.impl;

import java.util.Arrays;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.vavr.Lazy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.raisercostin.nodes.JacksonNodes;

public class JsonNodes implements JacksonNodes, JacksonNodesLike<JsonNodes, JsonMapper, FormatSchema> {
  public final Lazy<JsonMapper> mapper;

  public JsonNodes() {
    this(Lazy.of(() -> JacksonUtils.configure(new JsonMapper())));
  }

  public JsonNodes(Lazy<JsonMapper> mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonMapper mapper() {
    return mapper.get();
  }

  @Override
  public JsonNodes create(JsonMapper mapper) {
    return new JsonNodes(Lazy.of(() -> mapper));
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonNodes createJacksonNodes(ObjectMapper mapper) {
    return create((JsonMapper) mapper);
  }

  // TODO doesn't work for xml
  public static PrettyPrinter createJsonPrettyPrinter(String ident) {
    // Setup a pretty printer with an indenter (indenter has 4 spaces in this case)
    // DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("", DefaultIndenter.SYS_LF);
    DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter(ident, "\n");
    DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
    printer.indentObjectsWith(indenter);
    printer.indentArraysWith(indenter);
    return printer;//.withoutSpacesInObjectEntries();
  }
  //  DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
  //  prettyPrinter.indentArraysWith(new DefaultIndenter("  ", "\n"));
  //  Nodes.json.mapper.setDefaultPrettyPrinter(prettyPrinter);
}
