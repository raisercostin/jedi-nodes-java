package org.raisercostin.nodes.impl;

import java.util.List;

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

  @Override
  default <T> T toObject(String content, Class<T> clazz) {
    return ExceptionUtils.tryWithSuppressed(() -> mapper().readValue(content, clazz),
      "Cannot deserialize [%s] to [%s].", content, clazz);
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

  default <T extends Nodes> T excluding(String... excludedFields) {
    final ObjectMapper mapper = mapper().copy();
    JacksonUtils.configureExclusions(mapper, excludedFields);
    return newNodes(mapper);
  }

  default <T extends Nodes> T parseWithFailOnUnknwon() {
    return newNodes(JacksonUtils.configure(mapper().copy(), true));
  }

  <T extends JacksonNodes> T newNodes(ObjectMapper configure);
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
