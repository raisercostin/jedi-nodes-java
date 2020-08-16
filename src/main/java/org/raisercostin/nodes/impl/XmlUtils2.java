package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlUtils2 implements JacksonNodes, JacksonNodesLike<XmlUtils2, XmlMapper, FormatSchema> {
  private final XmlMapper mapper;

  public XmlUtils2() {
    this(JacksonUtils.configure(new XmlMapper()));
  }

  public XmlUtils2(XmlMapper mapper) {
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
    return mapper;
  }

  @Override
  public XmlUtils2 newNodes(XmlMapper configure) {
    return new XmlUtils2(configure);
  }
}
