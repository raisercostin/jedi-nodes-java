package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vavr.Lazy;
import org.raisercostin.nodes.JacksonNodes;

public class XmlJacksonNodes implements JacksonNodes, JacksonNodesLike<XmlJacksonNodes, XmlMapper, FormatSchema> {
  private final Lazy<XmlMapper> mapper;

  public XmlJacksonNodes() {
    this(Lazy.of(() -> JacksonUtils.configure(new XmlMapper())));
  }

  public XmlJacksonNodes(Lazy<XmlMapper> mapper) {
    this.mapper = mapper;
  }
  // mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false);
  // private static final XmlMapper mapper = createJaxbObjectMapper();
  //
  // private static XmlMapper createJaxbObjectMapper() {
  // XmlMapper mapper = new XmlMapper();
  // final TypeFactory typeFactory = TypeFactory.defaultInstance();
  // final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(typeFactory);
  // // // make deserializer use JAXB annotations (only)
  // // mapper.getDeserializationConfig().with(introspector);
  // // // make serializer use JAXB annotations (only)
  // // mapper.getSerializationConfig().with(introspector);
  // mapper.registerModule(new JaxbAnnotationModule());
  // // mapper.setAnnotationIntrospector(introspector);
  // return mapper;
  // }

  @SuppressWarnings("unchecked")
  @Override
  public XmlMapper mapper() {
    return mapper.get();
  }

  @Override
  public XmlJacksonNodes create(XmlMapper configure) {
    return new XmlJacksonNodes(Lazy.of(() -> configure));
  }

  @SuppressWarnings("unchecked")
  @Override
  public XmlJacksonNodes createJacksonNodes(ObjectMapper mapper) {
    return create((XmlMapper) mapper);
  }
}
