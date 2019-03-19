package top.wboost.common.system.spring.properties.check;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用配置项验证功能
 * @className EnablePropertiesChecker
 * @author jwSun
 * @date 2018年6月22日 上午11:34:01
 * @version 1.0.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PropertiesChecker.class)
public @interface EnablePropertiesChecker {

}