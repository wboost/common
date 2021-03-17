package top.wboost.common.boot.config;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @Auther: jwsun
 * @Date: 2021/3/17 20:49
 */
public class FrameworkSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {


    @Override
    public void starting() {
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            // 获得打印文本
        } catch (Exception e) {

        }

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "top.wboost.common.boot.logger.LogbackLoggingSystemSupport");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
