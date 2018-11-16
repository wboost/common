package top.wboost.common.boot.config;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import top.wboost.common.base.ConfigForBase;
import top.wboost.common.base.annotation.AutoConfig;
import top.wboost.common.boot.util.SpringBootUtil;
import top.wboost.common.util.StringUtil;
import top.wboost.common.utils.web.utils.PropertiesUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 增加spring-boot初始化
 * 更改扫描包,增加支持{@link AutoConfig}
 * @className ConfigForCommonBootApplicationContextInitializer
 * @see top.wboost.common.base.annotation.AutoConfig
 * @author jwSun
 * @date 2018年3月28日 下午4:11:50
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(CommonProperties.class)
public class ConfigForCommonBootApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

//    @Autowired
//    CommonProperties commonProperties;

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        boolean isBoot = false;
        if (context instanceof AnnotationConfigEmbeddedWebApplicationContext) {
            AnnotationConfigEmbeddedWebApplicationContext webContext = (AnnotationConfigEmbeddedWebApplicationContext) context;
            webContext.scan(scanPackages());
            try {
                Field field = webContext.getClass().getDeclaredField("scanner");
                field.setAccessible(true);
                ClassPathBeanDefinitionScanner scanner = (ClassPathBeanDefinitionScanner) field.get(webContext);
                scanner.addIncludeFilter(new AnnotationTypeFilter(AutoConfig.class));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
            try {
                webContext.setBeanNameGenerator(
                        (BeanNameGenerator) Class.forName(ConfigForBase.BEAN_NAME_GENERATOR_CLASS).newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            isBoot = true;
        } else if (context instanceof GenericWebApplicationContext) {
            try {
                SpringBootUtil.getLauncherClass();
                ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
                ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) beanFactory);
                scanner.addIncludeFilter(new AnnotationTypeFilter(AutoConfig.class));
                scanner.scan(scanPackages());
                isBoot = true;
            } catch (Exception e) {
                //no boot application.ignore
            }
        }
        if (isBoot) {
            //初始化获得启动类
            SpringBootUtil.getLauncherClass();
        }
    }

    private String[] scanPackages() {
        String[] initPackages = ConfigForBase.BasePackage.allPackages();
        String appPackage = ClassUtils.getPackageName(SpringBootUtil.getLauncherClass().getName());
        List<String> scanPackages = new ArrayList<>(Arrays.asList(initPackages));
        scanPackages.add(appPackage);
        String property = PropertiesUtil.getProperty("common.conf.add_scan_packages");
        if (StringUtil.notEmpty(property)) {
            JSONArray addPackages = JSONArray.parseArray(property);
            scanPackages.addAll(Arrays.asList(addPackages.toArray(new String[]{})));
        }
        return new HashSet<>(scanPackages).toArray(new String[]{});
    }
}
