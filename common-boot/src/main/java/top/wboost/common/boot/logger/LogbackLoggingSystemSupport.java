package top.wboost.common.boot.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import top.wboost.common.base.enums.CharsetEnum;
import top.wboost.common.util.ReflectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @Auther: jwsun
 * @Date: 2021/3/17 20:53
 */
public class LogbackLoggingSystemSupport extends LogbackLoggingSystem {

    private static PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private String defaultConfig = "classpath*:configure/logback-spring.xml";

    public LogbackLoggingSystemSupport(ClassLoader classLoader) {
        super(classLoader);
    }


    protected void loadDefaults(LoggingInitializationContext initializationContext,
                                LogFile logFile) {
        loadConfigurationByWboostDefault(initializationContext);
    }

    protected void loadConfigurationByWboostDefault(LoggingInitializationContext initializationContext) {

        try {
            Method getLoggerContext = ReflectUtil.findMethod(LogbackLoggingSystemSupport.class, "getLoggerContext");
            getLoggerContext.setAccessible(true);
            LoggerContext loggerContext = (LoggerContext) getLoggerContext.invoke(this);

            Method stopAndReset = ReflectUtil.findMethod(LogbackLoggingSystemSupport.class, "stopAndReset", LoggerContext.class);
            stopAndReset.setAccessible(true);
            stopAndReset.invoke(this,loggerContext);
            configureByJarClasspathUrl(initializationContext, loggerContext);
        } catch (Exception e) {
            throw new IllegalStateException("Logback config error.", e);
        }
    }

    private void configureByJarClasspathUrl(LoggingInitializationContext initializationContext, LoggerContext loggerContext) throws IOException, JoranException {
        System.out.println("-- configure default logback by wboost --");
        SpringBootJoranConfigurator configurator = new SpringBootJoranConfigurator(initializationContext);
        configurator.setContext(loggerContext);
        configurator.doConfigure(loadDefaultLogbackConfig());
    }

    private InputStream loadDefaultLogbackConfig() throws IOException {
        Resource[] resources = resourcePatternResolver.getResources(getDefaultConfig());
        if (resources.length > 0) {
            EncodedResource encodedResource = new EncodedResource(resources[0], CharsetEnum.UTF_8.getCharset());
            return encodedResource.getInputStream();
        } else {
            throw new RuntimeException("NO DEFAULT LOGBACK CONFIG");
        }
    }

    private String getDefaultConfig() {
        return defaultConfig;
    }

}
