package top.wboost.common.utils.web.core;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import top.wboost.common.base.enums.CharsetEnum;

import java.io.IOException;
import java.util.Properties;

public class PropertiesEncodePropertySourceLoader implements PropertySourceLoader, Ordered {

	@Override
	public String[] getFileExtensions() {
		return new String[] { "properties", "xml" };
	}

	@Override
	public PropertySource<?> load(String name, Resource resource, String profile)
			throws IOException {
		if (profile == null) {
			Properties properties = PropertiesLoaderUtils.loadProperties(new EncodedResource(resource, CharsetEnum.UTF_8.getCharset()));
			if (!properties.isEmpty()) {
				return new PropertiesPropertySource(name, properties);
			}
		}
		return null;
	}

	/**
	 * 优先于org.springframework.boot.env.PropertiesPropertySourceLoader
	 * @return
	 */
	@Override
	public int getOrder() {
		return 0;
	}
}