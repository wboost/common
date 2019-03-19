package top.wboost.common.annotation;

import top.wboost.common.system.code.SystemCode;

import java.lang.annotation.*;

/**
 * 方法说明注解
 * @className Explain
 * @author jwSun
 * @date 2017年6月26日 下午6:51:16
 * @version 1.0.0
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Explain {

    String value();

    /**
     * 方法说明,可支持参数占位符及top.wboost.common.log.entity.MethodLog中值,如方法说明为为根据某个id查询详细信息，如
     *
     * @Explain(value="根据id查询详细信息",description="用户{createId},ip为{ip},在{createTime}时访问了{methodName}方法,查询了id为{id}的详细信息") public ResultEntity findById(String id)
     */
    String description() default "";

    int exceptionCode() default 0;

    SystemCode systemCode() default SystemCode.NOTHING;

}