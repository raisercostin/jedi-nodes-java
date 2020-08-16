package org.raisercostin.nodes.impl;

import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import org.raisercostin.nodes.ExceptionUtils;
import org.raisercostin.nodes.Nodes;

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
    ObjectMapper mapper = mapper();
    if (this instanceof PropUtils2 && mapper.getDeserializationConfig().getFullRootName() != null) {
      String prefix = mapper.getDeserializationConfig().getFullRootName().getSimpleName();
      //if some properties doesn't have to rootName they will not be ignored
      //com.fasterxml.jackson.databind.exc.MismatchedInputException: Root name 'field1' does not match expected ('prefix1') for type [simple type, class org.raisercostin.nodes.ReadingWithRootNameTest$Dummy1]
      String newContent = content.replaceAll("(?m)^(?!" + Pattern.quote(prefix) + ").*$", "");
      log.debug("Removing lines that doesn't start with [{}] from [{}] and getting [{}]", prefix, content, newContent);
      content = newContent;
    }
    String content2 = content;
    return ExceptionUtils.tryWithSuppressed(() -> mapper().readValue(content2, clazz),
      "Cannot deserialize [%s] to [%s].", content2, clazz);
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
  default Map<String, Object> toMap(Object payload) {
    return Nodes.json.toObject(Nodes.json.toString(payload), Map.class);
  }
}
