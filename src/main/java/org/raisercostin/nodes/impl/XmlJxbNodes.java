package org.raisercostin.nodes.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.raisercostin.nodes.Nodes;

/**Pure Jxb handler.*/
public class XmlJxbNodes implements Nodes {
  private static final LoadingCache<Class<?>, JAXBContext> contexts = CacheBuilder.newBuilder()
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
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      // format the XML output
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      // if want to marshal values without `@XmlRootElement` https://codenotfound.com/jaxb-marshal-element-missing-xmlrootelement-annotation.html
      //QName qName = new QName("com.yourModel.t", "object");
      //JAXBElement<T> root = new JAXBElement<>(qName, (Class<T>) value.getClass(), value);
      Object root = value;
      StringWriter stringWriter = new StringWriter();
      jaxbMarshaller.marshal(root, stringWriter);
      String result = stringWriter.toString();
      return result;
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
   * @throws ExecutionException
   * @threadsafe Search for a context for this class.
   */
  private static <T> JAXBContext findContext(Class<T> clazz) {
    try {
      JAXBContext context = contexts.get(clazz);
      Preconditions.checkNotNull(context, "Context for [" + clazz + "] should always be created by cache.");
      return context;
    } catch (ExecutionException e) {
      throw ExceptionUtils.nowrap(e);
    }
  }
}
