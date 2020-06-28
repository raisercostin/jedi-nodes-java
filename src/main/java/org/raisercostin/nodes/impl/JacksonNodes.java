package org.raisercostin.nodes.impl;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import org.raisercostin.nodes.ExceptionUtils;
import org.raisercostin.nodes.Nodes;

public interface JacksonNodes extends Nodes {
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
    return ExceptionUtils.tryWithSuppressed(() -> mapper().readValue(content, clazz),
      "Cannot deserialize [%s] to [%s].", content, clazz);
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
