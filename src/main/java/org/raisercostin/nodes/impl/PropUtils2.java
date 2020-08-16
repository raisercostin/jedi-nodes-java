package org.raisercostin.nodes.impl;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;

public class PropUtils2 implements JacksonNodes, JacksonNodesLike<PropUtils2, JavaPropsMapper, JavaPropsSchema> {
  private final JavaPropsMapper mapper;
  private JavaPropsSchema defaultSchema = null;

  public PropUtils2() {
    this(JacksonUtils.configure(new JavaPropsMapper()));
  }

  public PropUtils2(JavaPropsMapper mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JavaPropsMapper mapper() {
    return mapper;
  }

  @Override
  public PropUtils2 newNodes(JavaPropsMapper mapper) {
    return new PropUtils2(mapper);
  }

  @Override
  public JavaPropsSchema schema() {
    return defaultSchema;
  }

  @Override
  public PropUtils2 withSchema(JavaPropsSchema formatSchema) {
    this.defaultSchema = formatSchema;
    return this;
  }

  @Override
  public PropUtils2 withPrefix(String rootName) {
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
