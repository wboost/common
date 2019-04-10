import org.junit.Test;
import org.springframework.core.env.PropertySource;
import top.wboost.common.es.entity.EsResultEntity;
import top.wboost.common.es.search.EsAggregationSearch;
import top.wboost.common.es.search.EsFieldSearch;
import top.wboost.common.es.search.EsSearch;
import top.wboost.common.es.util.EsQueryUtil;
import top.wboost.common.utils.web.core.ConfigProperties;
import top.wboost.common.utils.web.utils.PropertiesUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Auther: jwsun
 * @Date: 2019/3/28 18:10
 */
public class EsUtilTest {

    private void config() {
        PropertySource<?> propertySource = PropertiesUtil.loadPropertySource("example/es.properties");
        new ConfigProperties().setEmbeddedValueResolver(str -> propertySource.getProperty(str.substring(2).substring(0,str.length() - 3)).toString());
    }

    @Test
    public void checkFieldSearch() {
        config();
        EsFieldSearch esFieldSearch = new EsFieldSearch("cap_ccrc", "ccrc");
        esFieldSearch.putField("car_type").putField("car_num");
        EsResultEntity entity = EsQueryUtil.queryFieldList(esFieldSearch, null);
        System.out.println(entity);
    }

    @Test
    public void checkSearch() {
        config();
        EsSearch esFieldSearch = new EsSearch("cap_ccrc", "ccrc");
        EsResultEntity entity = EsQueryUtil.querySimpleList(esFieldSearch, null);
        System.out.println(entity);
    }

    @Test
    public void test() throws InterruptedException {
        LinkedBlockingQueue<Object> objects = new LinkedBlockingQueue<>(2);
        objects.put(1);
        objects.put(2);
        objects.put(3);
        objects.put(4);
        objects.put(5);
        System.out.println(objects.take());
        System.out.println(objects.take());
        System.out.println(objects.take());
        System.out.println(objects.take());
        System.out.println(objects.take());
        System.out.println(objects.take());
    }

    @Test
    public void checkTopHits() {
        config();
        EsAggregationSearch esAggregationSearch = new EsAggregationSearch("gateway-log-all", "log" ).setField("startTime");

        EsResultEntity entity = EsQueryUtil.queryAggregationList(esAggregationSearch, null);
        System.out.println(entity);
    }


}
