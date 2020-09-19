package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import org.raisercostin.nodes.JacksonNodes;

public class PropNodes implements JacksonNodes, JacksonNodesLike<PropNodes, JavaPropsMapper, JavaPropsSchema> {
  private final JavaPropsMapper mapper;
  private JavaPropsSchema defaultSchema = null;

  public PropNodes() {
    this(JacksonUtils.configure(new JavaPropsMapper()));
  }

  public PropNodes(JavaPropsMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JavaPropsMapper mapper() {
    return mapper;
  }

  @Override
  public PropNodes create(JavaPropsMapper mapper) {
    return new PropNodes(mapper);
  }

  @SuppressWarnings("unchecked")
  @Override
  public PropNodes createJacksonNodes(ObjectMapper mapper) {
    return create((JavaPropsMapper) mapper);
  }

  @Override
  public JavaPropsSchema schema() {
    return defaultSchema;
  }

  @Override
  public PropNodes withSchema(JavaPropsSchema formatSchema) {
    this.defaultSchema = formatSchema;
    return this;
  }

  @Override
  public PropNodes withPrefix(String rootName) {
    return withSchema(JavaPropsSchema.emptySchema()
      .withPrefix(rootName)
    //.withoutPathSeparator()
    );
    //        .withMapper(mapper -> {
    //          //mapper.getDeserializationConfig()
    //          //mapper.setConfig(mapper.getDeserializationConfig().withRootName(rootName));
    //          mapper.setPropertyNamingStrategy(new PrefixStrategy(rootName + "."));
    //          return mapper;
    //        });
  }
}
