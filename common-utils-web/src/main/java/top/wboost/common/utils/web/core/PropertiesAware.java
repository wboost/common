package top.wboost.common.utils.web.core;

import org.springframework.web.context.support.StandardServletEnvironment;

public interface PropertiesAware {

    public void setProperties(StandardServletEnvironment localenv);

}
