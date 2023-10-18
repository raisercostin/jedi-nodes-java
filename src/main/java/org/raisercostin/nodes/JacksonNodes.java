package org.raisercostin.nodes;

import java.util.List;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import io.vavr.Function1;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.raisercostin.nodes.impl.ExceptionUtils;
import org.raisercostin.nodes.impl.JsonNodes;

public interface JacksonNodes extends Nodes {
  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JacksonNodes.class);

  @Override
  default <T> String toString(T value) {
    return ExceptionUtils.tryWithSuppressed(() -> mapper().writeValueAsString(value), "Cannot serialize [%s]: ", value);
  }

  /**To map to a generic type List, Map use TypeReference:
   * <pre>
       TypeReference<Map<String, DataSpec>> typeRef = new TypeReference<Map<String, DataSpec>>(){};
       Map<String, DataSpec> map1 = toObject(content,typeRef);
     </pre>
   */
  @Override
  default <T> T toObject(String content, Class<T> clazz) {
    return toObject(content, 1000, clazz);
  }

  default <T> T toObject(String content, int maxSize, Class<T> clazz) {
    //ObjectMapper mapper = mapper();
    //    if (this instanceof PropUtils2 && mapper.getDeserializationConfig().getFullRootName() != null) {
    //      String prefix = mapper.getDeserializationConfig().getFullRootName().getSimpleName();
    //      //if some properties doesn't have to rootName they will not be ignored
    //      //com.fasterxml.jackson.databind.exc.MismatchedInputException: Root name 'field1' does not match expected ('prefix1') for type [simple type, class org.raisercostin.nodes.ReadingWithRootNameTest$Dummy1]
    //      String newContent = content.replaceAll("(?m)^(?!" + Pattern.quote(prefix) + ").*$", "");
    //      log.debug("Removing lines that doesn't start with [{}] from [{}] and getting [{}]", prefix, content, newContent);
    //      content = newContent.trim();
    //      //add a dummy value in case is empty. anyway should be ignored
    //      if (content.isEmpty()) {
    //        content = prefix + ".dummy=1";
    //      }
    //    }
    String content2 = content;
    return ExceptionUtils.tryWithSuppressed(() -> {
      FormatSchema schema = schema();
      //System.out.println("schema1:" + schema);
      if (schema == null) {
        return mapper().readValue(content2, clazz);
      } else {
        ObjectReader reader = mapper().readerFor(clazz);
        return reader.with(schema).readValue(content2);
      }
    }, "Cannot deserialize [%s] to [%s].", StringUtils.abbreviateMiddle(content2, "[...]", maxSize), clazz);
  }

  default String prettyPrint(String content) {
    return ExceptionUtils.tryWithSuppressed(() -> {
      return prettyPrint2(content);
    }, "Cannot prettyPrint [%s]: ", StringUtils.abbreviateMiddle(content, "[...]", 2000));
  }

  default String prettyPrint1(String content) throws JsonProcessingException, JsonMappingException {
    //TODO This is more performant? but does not exclude fields.
    JsonNode value = mapper().readTree(content);
    return mapper().writeValueAsString(value);
  }

  default String prettyPrint2(String content) throws JsonProcessingException, JsonMappingException {
    return toString(toObject(content, Object.class));
  }

  default FormatSchema schema() {
    return null;
  }

  default JsonSchema jsonSchema(Class<?> clazz) {
    try {
      SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
      mapper().acceptJsonFormatVisitor(clazz, visitor);
      JsonSchema jsonSchema = visitor.finalSchema();
      return jsonSchema;
    } catch (JsonMappingException e) {
      throw ExceptionUtils.wrap(e);
    }
  }

  @Override
  default <T> T toObject(String content, TypeReference<T> typeRef) {
    return ExceptionUtils.tryWithSuppressed(() -> mapper().readValue(content, typeRef),
      "Cannot deserialize [%s] to [%s].", content, typeRef);
  }

  default <T> MappingIterator<T> toMappingIterator(String content, Class<T> clazz) {
    return ExceptionUtils.tryWithSuppressed(() -> mapper().readerFor(clazz).readValues(content),
      "Cannot deserialize [%s] to [%s].",
      content, clazz);
  }

  default <T> List<T> toList(String content, Class<T> clazz) {
    return ExceptionUtils.tryWithSuppressed(() -> toMappingIterator(content, clazz).readAll(),
      "Cannot deserialize [%s] to [%s].", content, clazz);
  }

  default <T> Iterator<T> toIterator(String content, Class<T> clazz) {
    return Iterator.ofAll(toMappingIterator(content, clazz));
  }

  /** In case jackson is used and more flexibility is needed. */
  <T extends ObjectMapper> T mapper();

  // public static final ObjectMapper mapper = JacksonUtils.createObjectMapper();
  // public static final ObjectMapper mapperWithFailOnUnknwon = JacksonUtils.createObjectMapper(true);
  /*
   * public static <T> T parseJson(String value, Class<T> clazz) { try { return mapper.readValue(value, clazz); } catch
   * (IOException e) { throw new RuntimeException("Cannot deserialize from [" + value + "]: " + e.getMessage(), e); } }
   */
  /*
   * public static <T> T parseWithFailOnUnknwon(String response, Class<T> clazz) { try { return
   * mapperWithFailOnUnknwon.readValue(response, clazz); } catch (IOException e) { throw new
   * RuntimeException("Cannot deserialize from [" + response + "]: " + e.getMessage(), e); } }
   */
  /*
   * public JsonNode readJson(String content) { try { return mapper.readTree(content); } catch (JsonProcessingException
   * e) { throw org.raisercostin.util.ExceptionUtils.nowrap(e); } }
   */

  @SuppressWarnings("unchecked")
  default Map<String, Object> toMapFromObject(Object payload) {
    return toMapFromString(toString(payload));
  }

  @SuppressWarnings("unchecked")
  default Map<String, Object> toMapFromString(String jsonString) {
    return toObject(jsonString, Map.class);
  }

  @SuppressWarnings("unchecked")
  default <T extends JacksonNodes> T withObjectMapper(Function1<ObjectMapper, ObjectMapper> mapperChanger) {
    final ObjectMapper mapper = mapperChanger.apply(mapper());
    return createJacksonNodes(mapper);
  }

  <T extends JacksonNodes> T createJacksonNodes(ObjectMapper configure);
}
