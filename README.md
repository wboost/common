<img src="http://www.wboost.top/img/icon2.png" width="28" hegiht="28" align=center />QUICK DEV SUPPORT
================

[Home Page](http://www.wboost.top) | [官方主页](http://www.wboost.top) | [中文说明]() | [文档手册](http://www.wboost.top/framework/spring-boot-starter-support/docs)
------
[![Packagist](http://www.wboost.top/svg/version-3.1.4-SNAPSHOT-brightgreen.svg)](http://www.wboost.top)

[![Packagist](http://www.wboost.top/svg/spring-4.3.13.RELEASE-brightgreen.svg)](http://www.wboost.top)    [![Packagist](http://www.wboost.top/svg/springboot-1.5.9.RELEASE-brightgreen.svg)](http://www.wboost.top)   ![Packagist](http://www.wboost.top/svg/hibernate-5.0.12.Final-brightgreen.svg)
------
> ### 引入说明
- [支持传统web架构项目与springboot项目无缝切换](#传统web架构项目与springboot项目切换)
- [统一参数验证机制，可复用，自由规则](#统一参数验证机制，可复用，自由规则)
- 统一配置文件初始化机制
- 自定义接收参数转换器
- @AutoRootApplicationConfig,@AutoWebApplicationConfig > 扫描注解分离不同上下文
- @AutoProxy > 代理类注册功能 
- 统一返回值及异常处理，接口文档
- es/kylin/sql等工具类
- 敏捷开发
- 搭配[spring-boot-starter-support](http://192.168.1.244/jcpt/SPRING-BOOT-STARTER-SUPPORT) 更佳


------------
#### 传统web架构项目与springboot项目切换

```
<properties>
<tools-group-id>top.wboost</tools-group-id>
<tools-group-version>3.1.4-SNAPSHOT</tools-group-version>
</properties>
```

##### 根据项目类型引入
- 传统web

```
<dependency>
    <groupId>${tools-group-id}</groupId>
    <version>${tools-group-version}</version>
    <artifactId>common-boost</artifactId>
</dependency>
```

- springboot 

```
<dependency>
    <groupId>${tools-group-id}</groupId>
    <version>${tools-group-version}</version>
    <artifactId>common-boot</artifactId>
</dependency>
```

boot项目自动扫描top.wboost.common,com.chinaoly及SpringApplication类下目录

------------
#### 统一参数验证机制，可复用，自由规则
默认提供@NotNull(不能为空)与@NotEmpty(不能为空且不能为空字符串)注解
- 新建校验注解文件LowerThan,注解上增加@top.wboost.common.annotation.parameter.ParameterConfig

```
// Integer、Long小于指定数据校验实例
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ParameterConfig
public @interface LowerThan {
    int value();
}
```

- 新建校验器LowerThanChecker类,并注入ioc（@AutoWebApplicationConfig）,且实现top.wboost.common.annotation.parameter.ParameterConfigChecker接口

```
@AutoWebApplicationConfig
public class LowerThanChecker implements ParameterConfigChecker {

    @Override
    public Boolean check(Object source, Object... argName) {
        return true;
    }

    private void throwException(String argName, int big) {
        SystemCodeException e = new SystemCodeException(SystemCode.PROMPT);
        e.setPromptMessage(argName + "参数值不能大于" + big);
        throw e;
    }

    // 验证方法 source:参数 annotation:注解 args: 参数名
    @Override
    public Boolean check(Object source, Annotation annotation, Object... args) {
        LowerThan lowerThan = (LowerThan) annotation;
        if (source instanceof Integer) {
            int param = (int) source;
            if (param > lowerThan.value()) {
                throwException(args[0].toString(), lowerThan.value());
            }
        }
        return true;
    }

    //校验器对应注解
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return LowerThan.class;
    }

}
```

- 使用

```
@GetMapping("/example")
@Explain(systemCode = SystemCode.DO_FAIL, value = "example")
public ResultEntity callTimeResult(
        @ApiParam(required = true, value = "演示参数不为空且小于指定数字") @NotNull @LowerThan(20) Integer segmentNum) {
    return ResultEntity.success(SystemCode.DO_OK).setData(segmentNum).build();
}
```

- 返回数据

```
curl localhost:8080/example
{"info":{"code":10902,"message":"segmentNum 为空"},"status":1}
```

```
curl localhost:8080/example?segmentNum=21
{"info":{"code":10913,"message":"segmentNum参数值不能大于20","systemCode":"PROMPT"},"status":1}
```

```
curl localhost:8080/example?segmentNum=10
{"data":10,"info":{"code":10906,"message":"执行成功","systemCode":"DO_OK"},"status":0}
```


待续.
