package top.wboost.common.system.spring.properties.check;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 配置值检测,检测失败抛出异常,需要在spring中注册
 * @author jwSun
 * @date 2017年6月12日 下午4:32:03
 */
@Data
@ConfigurationProperties("common.check.properties")
public class PropertiesCheckerProp {

    private List<String> names;


}
