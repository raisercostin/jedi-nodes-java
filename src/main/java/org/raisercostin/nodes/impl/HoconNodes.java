package org.raisercostin.nodes.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigResolveOptions;
import com.typesafe.config.ConfigSyntax;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.raisercostin.nodes.Nodes;

@Slf4j
@lombok.EqualsAndHashCode(onlyExplicitlyIncluded = true)
//@lombok.NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
@lombok.ToString
@lombok.With
@lombok.Builder(access = AccessLevel.PRIVATE)
@lombok.experimental.FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
@lombok.Getter(value = AccessLevel.NONE)
@lombok.Setter(value = AccessLevel.NONE)
public class HoconNodes implements Nodes {
  private static PropNodes properties = Nodes.prop.withCopyMapper();
  @Builder.Default
  public boolean useSystemEnvironment = false;
  @Builder.Default
  public boolean useSystemProperties = false;
  @Builder.Default
  public boolean dump = false;
  @Builder.Default
  public String originDescription = "contentAsString";
  @Builder.Default
  public ConfigSyntax syntax = ConfigSyntax.CONF;

  @Override
  public <T> String toString(T value) {
    Config config = value instanceof Config ? (Config) value : toConfigFromProperties(properties.toString(value));
    String newConfig = config.root()
      .render(
        ConfigRenderOptions.defaults().setOriginComments(true).setComments(true).setFormatted(true).setJson(false));
    return newConfig;
  }

  public Config toConfigFromProperties(String content) {
    return toConfig(content, ConfigSyntax.PROPERTIES);
  }

  public Config toConfigFromHocon(String content) {
    return toConfig(content, ConfigSyntax.CONF);
  }

  public Config toConfigFromJson(String content) {
    return toConfig(content, ConfigSyntax.JSON);
  }

  @Override
  public <T> T toObject(String content, Class<T> clazz) {
    Config config = toConfig(content, ConfigSyntax.CONF);
    if (clazz.isAssignableFrom(Config.class)) {
      return (T) config;
    } else {
      content = hoconToProperties(config);
      return properties.toObject(content, clazz);
    }
  }

  public Config toConfig(String content, ConfigSyntax configSyntax) {
    Config config = ConfigFactory
      .parseString(content,
        ConfigParseOptions.defaults().setSyntax(configSyntax).setOriginDescription(originDescription));
    if (useSystemProperties) {
      config = config.withFallback(ConfigFactory.systemProperties());
    }
    if (useSystemEnvironment) {
      config = config.withFallback(ConfigFactory.systemEnvironment());
    }
    config = config
      .resolve(ConfigResolveOptions.noSystem().setUseSystemEnvironment(false).setAllowUnresolved(false));
    if (dump) {
      String newConfig = config.root()
        .render(
          ConfigRenderOptions.defaults().setOriginComments(true).setComments(true).setFormatted(true).setJson(false));
      log.info("Config from {} with fallback to properties and environment: {}", originDescription, newConfig);
    }
    return config;
  }

  public HoconNodes withIgnoreUnknwon() {
    properties = properties.withIgnoreUnknwon();
    return this;
  }

  public HoconNodes withPrefix(String prefix) {
    properties = properties.withPrefix(prefix);
    return this;
  }

  public static String hoconToProperties(Config config) {
    //      String newConfig = config.root()
    //        .render(
    //          ConfigRenderOptions.defaults().setOriginComments(true).setComments(true).setFormatted(true).setJson(false));
    //      System.out.println(newConfig);
    Map<String, Object> allProperties2 = Iterator.ofAll(config.resolve().entrySet())
      .toSortedMap(x -> x.getKey(), x -> x.getValue().unwrapped());
    String allProperties = properties.toString(allProperties2);
    //
    //    log.info("a2:" + properties.toString(allProperties2));
    //    log.info("a1:" + allProperties2.mkString("\n"));
    //    String allProperties = Iterator.ofAll(config.entrySet())
    //      .toList()
    //      .sortBy(x -> x.getKey())
    //      .map(e -> e.getKey() + "=" + StringUtils.unwrap(e.getValue().render(), "\""))
    //      .mkString("\n");
    //log.info("all:\n{}", allProperties);
    return allProperties;
  }
}
