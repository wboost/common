package top.wboost.common.log.core;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import top.wboost.common.base.annotation.AutoRootApplicationConfig;
import top.wboost.common.base.core.ExecutorsDaemon;
import top.wboost.common.log.entity.Logger;
import top.wboost.common.log.entity.MethodLog;
import top.wboost.common.log.interfaces.LogManager;
import top.wboost.common.log.util.LoggerUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 方法日志管理器
 * @className MethodLogManager
 * @author jwSun
 * @date 2017年7月4日 下午5:59:36
 * @version 1.0.0
 */
@AutoRootApplicationConfig("defaultMethodLogManager")
public class MethodLogManager implements LogManager<MethodLog>, ApplicationListener<ContextClosedEvent> {

    private Logger log = LoggerUtil.getLogger(getClass());

    private ExecutorService sendLogService = Executors.newFixedThreadPool(2,
            ExecutorsDaemon.getDaemonThreadFactory("methodLogManagerPool"));

    @Override
    public boolean sendLog(MethodLog methodLog) {
        sendLogService.execute(() -> {
            log.info("sendLog...: {}", methodLog);
        });
        return false;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("close sendLogService");
        if (!sendLogService.isShutdown()) {
            sendLogService.shutdown();
        }
    }

}
