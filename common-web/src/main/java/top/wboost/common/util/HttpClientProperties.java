package top.wboost.common.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("common.util.httpClientUtil")
public class HttpClientProperties {

    /**每个路由最大连接数**/
    public int MAX_ROUTE_CONNECTIONS = 50;
   /**连接超时时间**/
    public int CONNECT_TIMEOUT = 10000;// 10s
    /**读取超时时间**/
    public int READ_TIMEOUT = 10000;// 10s
    /**允许跳转**/
    public boolean REDIRECTS_ENABLED = true;
    /** 获取连接的最大等待时间**/
    public int WAIT_TIMEOUT = 30000;// 30s
    /**最大连接数**/
    private int MAX_TOTAL_CONNECTIONS = 100;

}
