package org.raisercostin.nodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.raisercostin.nodes.impl.CsvUtils2;
import org.raisercostin.nodes.impl.GsonUtils2;
import org.raisercostin.nodes.impl.JsonUtils2;
import org.raisercostin.nodes.impl.JxbUtils2;
import org.raisercostin.nodes.impl.PropUtils2;
import org.raisercostin.nodes.impl.XmlUtils2;
import org.raisercostin.nodes.impl.YmlUtils2;

public interface Nodes {
  YmlUtils2 yml = new YmlUtils2();
  XmlUtils2 xml = new XmlUtils2();
  JsonUtils2 json = new JsonUtils2();
  GsonUtils2 gson = new GsonUtils2();
  JxbUtils2 jxb = new JxbUtils2();
  CsvUtils2 csv = new CsvUtils2();
  PropUtils2 prop = new PropUtils2();
  // TODO to add
  // hocon

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
