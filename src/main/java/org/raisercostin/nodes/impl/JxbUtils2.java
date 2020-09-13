package org.raisercostin.nodes.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;

import javax.xml.bind.JAXBContext;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.raisercostin.nodes.ExceptionUtils;
import org.raisercostin.nodes.Nodes;

public class JxbUtils2 implements Nodes {
  private static final Cache<Class<?>, JAXBContext> contexts = CacheBuilder.newBuilder()
    .expireAfterAccess(Duration.ofHours(10))
    .build(new CacheLoader<Class<?>, JAXBContext>()
      {
        @Override
        public JAXBContext load(Class<?> classAsKey) throws Exception {
          return JAXBContext.newInstance(classAsKey);
        }
      });

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
    JAXBContext context = contexts.getIfPresent(clazz);
    Preconditions.checkNotNull(context, "Context for [" + clazz + "] should always be createed by cache.");
    return context;
  }
}
