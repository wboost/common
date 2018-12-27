package top.wboost.common.es.repository;

import org.aopalliance.intercept.MethodInvocation;
import org.elasticsearch.index.query.QueryBuilders;
import top.wboost.common.context.config.AutoProxy;
import top.wboost.common.log.entity.Logger;
import top.wboost.common.log.util.LoggerUtil;

import java.util.ArrayList;
import java.util.Map;

public class EsRepositoryProxy implements AutoProxy {

    private Logger log = LoggerUtil.getLogger(getClass());

    public AutoProxy getObject(Class<?> clazz, Map<String, Object> config) throws Exception {
        EsRepositoryProxy proxy = new EsRepositoryProxy();
        QueryBuilders.termQuery("s", new ArrayList<String>());
        return proxy;
    }

    @Override public Object invoke(MethodInvocation invocation) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }

}