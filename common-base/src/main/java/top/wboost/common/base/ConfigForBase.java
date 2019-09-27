package top.wboost.common.base;

public class ConfigForBase {

    /**
     * 默认BeanNameGenerator
     **/
    public static final String BEAN_NAME_GENERATOR_CLASS = "top.wboost.common.context.generator.ConfigAnnotationBeanNameGenerator";


    public enum BasePackage {
        WBOOST("top.wboost.common"), CHINAOLY("com.chinaoly");
        String packageName;

        private BasePackage(String packageName) {
            this.packageName = packageName;
        }

        public static String[] allPackages() {
            BasePackage[] allEnum = BasePackage.values();
            String[] all = new String[allEnum.length];
            for (int i = 0; i < allEnum.length; i++) {
                all[i] = allEnum[i].packageName;
            }
            return all;
        }

    }

    public interface PropertiesConfig {
        // 是否开发者模式(部分common日志/返回数据包含)
        public final String IS_DEBUG = "common.dev.debug";
        // 处理异常返回码
        public final String EXCEPTION_STATUS = "common.config.exception.status";
        // 展示sql
        public final String SHOW_SQL = "common.dev.show-sql";
    }

    public interface SCAN_CONFIG {
        public final String WEB = "#web";
        public final String ROOT = "#root";
        public final String BOOT = "#boot";
    }

}

