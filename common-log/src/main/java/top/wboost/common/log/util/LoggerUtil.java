package top.wboost.common.log.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.util.COWArrayList;
import org.slf4j.LoggerFactory;
import top.wboost.common.log.entity.Logger;
import top.wboost.common.log.entity.LoggerTemplate;
import top.wboost.common.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志工具类
 * @className LoggerUtil
 * @author jwSun
 * @date 2017年6月30日 上午11:29:19
 * @version 1.0.0
 */
public class LoggerUtil {

    /**
     * 获得包装Logger类
     * @param clazz 使用logger类
     * @return
     */
    public static Logger getLogger(Class<?> clazz) {
        return (Logger) Proxy.newProxyInstance(LoggerTemplate.class.getClassLoader(),
                LoggerTemplate.class.getInterfaces(), new LoggerInvocationHandler(LoggerFactory.getLogger(clazz)));
    }

    /**
     * logback file
     *
     * @return
     */
    public static List<String> getLoggerFile() {
        org.slf4j.Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
        LoggerContext loggerContext = ReflectUtil.getFieldValue(logger, "loggerContext", LoggerContext.class);
        org.slf4j.Logger root = ReflectUtil.getFieldValue(loggerContext, "root", org.slf4j.Logger.class);
        AppenderAttachableImpl aai = ReflectUtil.getFieldValue(root, "aai", AppenderAttachableImpl.class);
        COWArrayList<OutputStreamAppender> appenderList = ReflectUtil.getFieldValue(aai, "appenderList", COWArrayList.class);
        List<String> logFiles = new ArrayList<>();
        appenderList.forEach(appender -> {
            if (appender instanceof FileAppender) {
                FileAppender rollingFileAppender = (FileAppender) appender;
                logFiles.add(rollingFileAppender.getFile());
            }
        });
        return logFiles;
    }



}

class LoggerInvocationHandler implements InvocationHandler {

    private org.slf4j.Logger logger;

    public LoggerInvocationHandler(org.slf4j.Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            result = logger.getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(logger, args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

}