package top.wboost.common.log.interfaces;

import top.wboost.common.log.entity.BaseLog;

/**
 * 日志管理接口
 * @className LogManager
 * @author jwSun
 * @date 2017年7月4日 下午5:31:19
 * @version 1.0.0
 */
public interface LogManager<T extends BaseLog> {

    boolean sendLog(T log);

}