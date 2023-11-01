package org.raisercostin.nodes;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vavr.control.Try;
import org.raisercostin.nodes.impl.CsvNodes;
import org.raisercostin.nodes.impl.GsonNodes;
import org.raisercostin.nodes.impl.HoconNodes;
import org.raisercostin.nodes.impl.JsonNodes;
import org.raisercostin.nodes.impl.PropNodes;
import org.raisercostin.nodes.impl.XmlJacksonNodes;
import org.raisercostin.nodes.impl.XmlJxbNodes;
import org.raisercostin.nodes.impl.XmlJxbThenJacksonNodes;
import org.raisercostin.nodes.impl.YmlNodes;

public interface Nodes {
  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Nodes.class);

  YmlNodes yml = new YmlNodes();
  JsonNodes json = new JsonNodes();
  GsonNodes gson = new GsonNodes();
  XmlJacksonNodes xml = new XmlJacksonNodes();
  XmlJxbNodes xmlJxb = new XmlJxbNodes();
  XmlJxbThenJacksonNodes xmlJxbThenJackson = Try.of(() -> new XmlJxbThenJacksonNodes())
    .onFailure(e -> log.warn("Cannot init jaxb.", e))
    .getOrNull();
  CsvNodes csv = new CsvNodes();
  PropNodes prop = new PropNodes();
  HoconNodes hocon = new HoconNodes();
  HoconNodes hoconWithSystem = new HoconNodes().withUseSystemEnvironment(true).withUseSystemProperties(true);
  @Deprecated
  XmlJxbNodes jxb = xmlJxb;

  <T> String toString(T value);

  <T> T toObject(String content, Class<T> clazz);

  default <T> T toObject(String content, TypeReference<T> typeRef) {
    throw new RuntimeException("Not implemented yet!!!");
  }

  @SuppressWarnings("unchecked")
  default <T> T clone(T value) {
    return (T) toObject(toString(value), value.getClass());
  }
}
