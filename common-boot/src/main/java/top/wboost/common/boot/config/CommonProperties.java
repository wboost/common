package top.wboost.common.boot.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

/**
 * boot通用配置类
 */
@Primary
@ConfigurationProperties("common.conf")
@Data
public class CommonProperties {


    /**
     * 增加扫描包
     */
    private String[] add_scan_packages = new String[]{};


}
