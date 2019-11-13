package org.raisercostin.nodes.impl;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.raisercostin.nodes.Nodes;
import org.raisercostin.util.ExceptionUtils;

public class JxbUtils2 implements Nodes {
  @Override
  public <T> String toString(T value) {
    return ExceptionUtils.tryWithSuppressed(() -> {
      StringWriter sw = new StringWriter();
      JAXBContext jaxbContext = findContext(value.getClass());
      jaxbContext.createMarshaller().marshal(value, sw);
      return sw.toString();
    }, "");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T toObject(String content, Class<T> clazz) {
    return ExceptionUtils.tryWithSuppressed(() -> {
      JAXBContext jaxbContext = findContext(clazz);
      StringReader sr = new StringReader(content);
      return (T) jaxbContext.createUnmarshaller().unmarshal(sr);
    }, "");
  }

  /**
   * @threadsafe Search for a context for this class.
   */
  private static <T> JAXBContext findContext(Class<T> clazz) {
    return ExceptionUtils.tryWithSuppressed(() -> {
      // TODO for now just create
      return JAXBContext.newInstance(clazz);
    }, "");
  }
}
