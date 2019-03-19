package top.wboost.common.system.spring.properties.check;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import top.wboost.common.log.entity.Logger;
import top.wboost.common.log.util.LoggerUtil;
import top.wboost.common.system.exception.SystemException;
import top.wboost.common.util.StringUtil;
import top.wboost.common.utils.web.utils.PropertiesUtil;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置值检测,检测失败抛出异常,需要在spring中注册
 * @author jwSun
 * @date 2017年6月12日 下午4:32:03
 */
//@AutoRootApplicationConfig("propertiesChecker")
@EnableConfigurationProperties(PropertiesCheckerProp.class)
public class PropertiesChecker implements InitializingBean, Ordered {

    private Logger log = LoggerUtil.getLogger(getClass());

    @Autowired
    PropertiesCheckerProp propertiesCheckerProp;

    /**不能为空字段 key:properties-name,val:properties-file**/
    private Map<String, String> notNullMap;
    /**可以为空字段,但必须有key key:properties-name,val:properties-file**/
    private Map<String, String> canNullMap;

    public Map<String, String> getNotNullMap() {
        return notNullMap;
    }

    public void setNotNullMap(Map<String, String> notNullMap) {
        this.notNullMap = notNullMap;
    }

    public Map<String, String> getCanNullMap() {
        return canNullMap;
    }

    public void setCanNullMap(Map<String, String> canNullMap) {
        this.canNullMap = canNullMap;
    }

    @PostConstruct
    public void checkProperties() {

    }

    public void afterPropertiesSet() throws Exception {
        log.info("properties config check init...");
        if (notNullMap == null) {
            notNullMap = new HashMap<>();
        }
        propertiesCheckerProp.getNames().forEach(name -> notNullMap.put(name, null));
        for (Map.Entry<String, String> nutNullEntry : notNullMap.entrySet()) {
            String propertiesVal = null;
            if ("".equals(nutNullEntry.getValue())) {
                propertiesVal = PropertiesUtil.getProperty(nutNullEntry.getKey());
            } else {
                propertiesVal = PropertiesUtil.getProperty(nutNullEntry.getKey(), nutNullEntry.getValue());
            }
            if (propertiesVal == null) {
                // 获取数组形式
                propertiesVal = PropertiesUtil.getProperty(nutNullEntry.getKey() + "[0]");
            }
            log.debug("find notNullMap properties : {} in {} , value is {}.", nutNullEntry.getKey(),
                    nutNullEntry.getValue(), propertiesVal);
            if (StringUtil.notEmpty(propertiesVal)) {
                continue;
            } else {
                throw new SystemException("notNullMap properties : " + nutNullEntry.getKey() + " in "
                        + nutNullEntry.getValue() + " ,value is null or empty.");
            }
        }
        if (canNullMap != null) {
            for (Map.Entry<String, String> canNullEntry : canNullMap.entrySet()) {
                String propertiesVal = null;
                if ("".equals(canNullEntry.getValue())) {
                    propertiesVal = PropertiesUtil.getProperty(canNullEntry.getKey());
                } else {
                    propertiesVal = PropertiesUtil.getProperty(canNullEntry.getKey(), canNullEntry.getValue());
                }
                log.debug("find canNullMap properties : {} in {} , value is {}.", canNullEntry.getKey(),
                        canNullEntry.getValue(), propertiesVal);
                if (propertiesVal == null) {
                    throw new SystemException("canNullMap properties : " + canNullEntry.getKey() + " in "
                            + canNullEntry.getValue() + " ,value is null.");
                }
            }
        }
        log.info("all properties check success");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
