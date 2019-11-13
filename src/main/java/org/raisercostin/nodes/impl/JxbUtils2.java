package org.raisercostin.nodes.impl;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import lombok.SneakyThrows;
import org.raisercostin.nodes.Nodes;

public class JxbUtils2 implements Nodes {
  @SneakyThrows
  @Override
  public <T> String toString(T value) {
    StringWriter sw = new StringWriter();
    JAXBContext jaxbContext = findContext(value.getClass());
    jaxbContext.createMarshaller().marshal(value, sw);
    return sw.toString();
  }

  @SneakyThrows
  @Override
  public <T> T toObject(String content, Class<T> clazz) {
    JAXBContext jaxbContext = findContext(clazz);
    StringReader sr = new StringReader(content);
    return (T) jaxbContext.createUnmarshaller().unmarshal(sr);
  }

  /**
   * @threadsafe Search for a context for this class.
   */
  @SneakyThrows
  private static <T> JAXBContext findContext(Class<T> clazz) {
    // TODO for now just create
    return JAXBContext.newInstance(clazz);
  }
}
