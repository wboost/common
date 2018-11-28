package top.wboost.common.utils.web.utils;

import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.AntPathMatcher;
import top.wboost.common.base.enums.CharsetEnum;
import top.wboost.common.exception.BusinessException;
import top.wboost.common.log.entity.Logger;
import top.wboost.common.log.util.LoggerUtil;
import top.wboost.common.util.StringUtil;
import top.wboost.common.utils.web.core.ConfigProperties;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class PropertiesUtil {

    private static Logger log = LoggerUtil.getLogger(PropertiesUtil.class);

    private static PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 获得配置文件参数
     * @param name 配置名
     * @return 配置值
     */
    public static String getProperty(String name) {
        return getPropertyOrDefault(name, null, null);
    }

    public static String getProperty(String name, String path) {
        return getPropertyOrDefault(name, path, null);
    }

    public static String getPropertyOrDefault(String name, String defaultVal) {
        return getPropertyOrDefault(name, null, defaultVal);
    }

    public static String getPropertyOrDefault(String name, String path, String defaultVal) {
        String val = null;
        try {
            if (!StringUtil.notEmpty(path)) {
                if (ConfigProperties.resolver != null) {
                    val = ConfigProperties.resolver.resolveStringValue("${" + name + "}");
                    if (val != null)
                        val = new String(val.getBytes(CharsetEnum.ISO_8859_1.getName()),
                                CharsetEnum.UTF_8.getCharset());
                } else {
                    val = ConfigProperties.localenv.getProperty(name, defaultVal);
                }
                if (("${" + name + "}").equals(val)) {
                    val = ConfigProperties.localenv.getProperty(name, defaultVal);
                    if (("${" + name + "}").equals(val)) {
                        val = null;
                    }
                }
            } else {
                try {
                    EncodedResource[] resources = loadResources(path);
                    for (EncodedResource resource : resources) {
                        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                        Object obj = properties.get(name);
                        if (obj != null) {
                            val = obj.toString();
                            break;
                        }
                    }
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn(e.getLocalizedMessage());
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return val == null ? defaultVal : val;
    }

    /**
     * 匹配通配符配置
     *
     * @param pattern 通配符*
     * @return
     */
    public static Map<String, Object> getPropertiesByPattern(String pattern) {
        Pattern patternDo = Pattern.compile(pattern);
        Map<String, Object> filterMap = new LinkedHashMap<>();
        PropertiesUtil.getAllProperties().forEach((key, val) -> {
            if (patternDo.matcher(key).find()) {
                filterMap.put(key, val);
            }
        });
        return filterMap;
    }

    public static Properties loadProperties(String location) {
        EncodedResource[] resources = loadResources(location);
        Properties prop = new Properties();
        try {
            for (EncodedResource resource : resources) {
                prop.putAll(PropertiesLoaderUtils.loadProperties(resource));
            }
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Properties();
    }

    /**
     * 获得指定前缀的集合
     * @param prefix 前缀
     */
    public static Map<String, Object> getPropertiesByPrefix(String prefix) {
        return getPropertiesByPrefix(prefix, getAllProperties());
    }

    public static Map<String, Object> getAllProperties() {
        Map<String, Object> retMap = new HashMap<>();
        Iterator<org.springframework.core.env.PropertySource<?>> ite = ConfigProperties.localenv.getPropertySources()
                .iterator();
        while (ite.hasNext()) {
            org.springframework.core.env.PropertySource<?> s = ite.next();
            retMap.putAll(resolvePropertySource(s));
        }
        return retMap;
    }

    public static Map<String, Object> resolvePropertySource(org.springframework.core.env.PropertySource<?> propertySource) {
        Map<String, Object> resolveMap = new HashMap<>();
        if (propertySource.getSource() instanceof Map) {
            resolveMap.putAll((Map<? extends String, ? extends String>) propertySource.getSource());
        } else if (propertySource instanceof EnumerablePropertySource) {
            EnumerablePropertySource<Collection<PropertySource<?>>> source = (EnumerablePropertySource<Collection<PropertySource<?>>>) propertySource;
            String[] nameArray = source.getPropertyNames();
            Arrays.asList(nameArray).forEach(name -> {
                resolveMap.put(name, propertySource.getProperty(name));
            });
        }
        return resolveMap;
    }

    private static EncodedResource[] loadResources(String location) {
        try {
            Resource[] resources = resourceResolver.getResources(location);
            EncodedResource[] encodeResources = new EncodedResource[resources.length];
            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                encodeResources[i] = new EncodedResource(resource, CharsetEnum.UTF_8.getCharset());
            }
            return encodeResources;
        } catch (Exception e) {
            log.error("loadResource error", e);
            throw new BusinessException("loadResource error");
        }
    }

    private static PropertySourcesLoader propertySourcesLoader = new PropertySourcesLoader();
    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    public static PropertySource<?> loadPropertySource(String location) {
        try {
            Resource bootstrapResource = resourceLoader.getResource(location);
            return propertySourcesLoader.load(bootstrapResource, "applicationConfig: [profile=]", "wboostConfigLoader: [" + location + "]", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> getPropertiesByPrefix(String prefix, PropertySource<?> propertySource) {
        return getPropertiesByPrefix(prefix, resolvePropertySource(propertySource));
    }

    public static Map<String, Object> getPropertiesByPrefix(String prefix, Map<String, Object> propMap) {
        Map<String, Object> retMap = new HashMap<>();
        for (Entry<String, Object> entry : propMap.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                retMap.put(entry.getKey(), entry.getValue());
            }
        }
        return retMap;
    }

    public static Object getPropertiesObject(String name) {
        return getAllProperties().get(name);
    }

    /**
     * 解析值中${} 为正确数据
     *
     * @param props
     * @return
     */
    public Map<String, Object> resolveProperties(Map<String, Object> props) {
        Map<String, Object> retMap = new HashMap<>();
        props.forEach((key, val) -> {
            if (val instanceof String)
                retMap.put(key, getProperty(key));
            else
                retMap.put(key, val);
        });
        return retMap;
    }

}
