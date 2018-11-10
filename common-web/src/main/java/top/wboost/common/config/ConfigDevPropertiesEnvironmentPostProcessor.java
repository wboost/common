package top.wboost.common.config;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import top.wboost.common.base.ConfigForBase;
import top.wboost.common.log.util.LoggerUtil;
import top.wboost.common.util.QuickHashMap;

import java.util.Map;

public class ConfigDevPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    //优先于ConfigFileApplicationListener
    public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    private PropertySourcesLoader propertySourcesLoader = new PropertySourcesLoader();
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private Logger logger = LoggerUtil.getLogger(ConfigDevPropertiesEnvironmentPostProcessor.class);


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 是否为开发者模式
        String isDebug = environment.getProperty(ConfigForBase.PropertiesConfig.IS_DEBUG);
        // 是否展示sql
        String showSql = environment.getProperty(ConfigForBase.PropertiesConfig.SHOW_SQL);
        Map<String,Object> devMap = new QuickHashMap<String,Object>().quickPut("spring.jpa.show-sql", showSql);
        environment.getPropertySources().addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new MapPropertySource("WBOOST_DEV_PROPERTIES", devMap));
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }


}
