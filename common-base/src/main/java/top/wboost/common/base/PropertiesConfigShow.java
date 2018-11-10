package top.wboost.common.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "common.dev")
public class PropertiesConfigShow {
    /**
     * 是否开发者模式(部分common日志/返回数据包含)
     **/
    private boolean debug = true;
    /**
     * 展示sql
     **/
    private boolean showSql = true;
}