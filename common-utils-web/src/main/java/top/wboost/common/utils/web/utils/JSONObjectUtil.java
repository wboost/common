package top.wboost.common.utils.web.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import top.wboost.common.log.entity.Logger;
import top.wboost.common.log.util.LoggerUtil;

import java.util.*;

/**
 * JSONObject扩展工具类
 * @className JSONObjectUtil
 * @author jwSun
 * @date 2017年8月2日 上午10:42:38
 * @version 1.0.0
 */
public class JSONObjectUtil {

    public static SerializeConfig serializeConfig = new SerializeConfig();

    private static Logger log = LoggerUtil.getLogger(JSONObjectUtil.class);
    private static boolean hasHibernate = false;
    static {
        SimpleDateFormatSerializer dateFormat = new SimpleDateFormatSerializer(DateUtil.DATE.YYYY_MM_DD_HH_MM_SS);
        serializeConfig.put(java.util.Date.class, dateFormat);
        serializeConfig.put(java.sql.Timestamp.class, dateFormat);
        try {
            Class.forName("org.hibernate.collection.internal.PersistentSet");
            Class.forName("org.hibernate.proxy.HibernateProxy");
            hasHibernate = true;
        } catch (ClassNotFoundException e) {
            // ignore
        }
    };

    /**
     * JSON转换排除lazy加载
     * @author jwSun
     * @date 2017年4月11日 上午10:52:06
     */
    public static PropertyFilter defaultSerializeFilter = (Object object, String name, Object value) -> {
        if (value == null) {
            return false;
        }
        return hasHibernate ? HibernateUtil.wasInitialized(value) : true;
    };

    public static PropertyFilter buildPropertyFilter(String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) {
            return defaultSerializeFilter;
        }

        Set<String> fieldNameList = new HashSet<>();
        Collections.addAll(fieldNameList, fieldNames);
        return (Object object, String name, Object value) -> {
            if (fieldNameList.contains(name) || value == null) {
                return false;
            }
            return hasHibernate ? HibernateUtil.wasInitialized(value) : true;
        };
    }

    /**
     * 与JSONObject.parseArray(String parse, Class<T> clazz)的区别
     * <pre>1.2.24版本parseArray转bean时调用的JavaBeanDeserializer在转换内部类时有BUG,,
     * 原因在于取内部类的类名时字符串处理有问题，最终会处理为java.util.ArrayList$xxx
     * 所以会131行Class.forName()时将会抛出异常
     * 此方法用于解决这种问题，具体实现逻辑为转为Object类型的ArrayList
     * 再将里面的数据一个个转化为要转的类型
     * </pre>
     * @param parse 要转化的文本
     * @param clazz 要转化的类型
     * @return
     */
    public static <T> List<T> parseArray(String parse, Class<T> clazz) {
        log.debug("parseArray:" + parse);
        JSONArray parseList = JSONObject.parseArray(parse);
        List<T> returnList = new ArrayList<>();
        //ObjectMapper om = new ObjectMapper();
        parseList.forEach((parseObj) -> {
            T parseEntity;
            //try {
            parseEntity = JSONObject.parseObject(parseObj.toString(), clazz);
            /*} catch (JSONException e) {
                try {
                    parseEntity = om.readValue(parseObj.toString(), clazz);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    parseEntity = null;
                }
            }*/
            returnList.add(parseEntity);
        });
        return returnList;
    }

    public static String toJSONString(Object obj, String... filterName) {
        return toJSONString(obj, null, filterName);
    }

    public static String toJSONString(Object obj, JSONConfig config, String... filterName) {
        List<SerializerFeature> serializerFeatureList = new ArrayList<>();
        if (config != null) {
            if (config.isDisableCircularReferenceDetect()) {
                serializerFeatureList.add(SerializerFeature.DisableCircularReferenceDetect);
            }
        }
        return JSONObject.toJSONString(obj, serializeConfig, buildPropertyFilter(filterName),
                serializerFeatureList.toArray(new SerializerFeature[serializerFeatureList.size()]));
    }

}
