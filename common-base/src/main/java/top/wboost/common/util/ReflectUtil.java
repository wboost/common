package top.wboost.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * @className ReflectUtil
 * @author jwSun
 * @date 2017年6月30日 上午11:36:09
 * @version 1.0.0
 */
public class ReflectUtil {

    enum MethodPrefix {
        get, set
    }

    private static Logger log = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 获得泛型类型
     * @param clazz  存在泛型的类
     * @param index 第几个泛型
     * @return 泛型类
     */
    public static Class<?> getGenericInterfaces(Class<?> clazz, int index) {
        Type[] type = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
        return (Class<?>) type[index];
    }

    /**
     * 获得属性的读方法
     * @author jwSun
     * @date 2017年3月29日 上午9:40:50
     * @param clazz 类
     * @param fieldName 属性名
     * @return 读方法
     */
    public static Method getReadMethod(Class<?> clazz, String fieldName) {
        try {
            Assert.notNull(fieldName);
            char cha = Character.toUpperCase(fieldName.charAt(0));
            //获得方法后名称
            String methodName = MethodPrefix.get + String.valueOf(cha) + fieldName.substring(1);
            return findMethod(clazz, methodName);
        } catch (Exception e) {
            log.info("getReadMethod ERROR: {}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 获得类的所有读方法
     * @param clazz  要获取的类
     * @return 读方法集合
     */
    public static List<Method> getAllReadMethod(Class<?> clazz, String... ignoreFieldNames) {
        List<Method> methodList = new ArrayList<>();
        try {
            Field[] fields = findFields(clazz);
            Set<String> set = null;
            if (ignoreFieldNames != null && ignoreFieldNames.length > 0) {
                set = new HashSet<>();
                Collections.addAll(set, ignoreFieldNames);
            }
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName()))
                    continue;
                if (set == null || set.contains(field.getName())) {
                    Method readMethod = ReflectUtil.getReadMethod(clazz, field.getName());
                    if (readMethod == null)
                        continue;
                    methodList.add(readMethod);
                }
            }
        } catch (Exception e) {
            log.info("getAllReadMethod ERROR: {}", e.getLocalizedMessage());
        }
        return methodList;
    }

    /**
     * 获得属性的set方法
     * @date 2017年3月29日 上午9:41:13
     * @param clazz   要获取的类
     * @param fieldName 属性名
     * @return 写方法
     */
    public static Method getWriteMethod(Class<?> clazz, String fieldName) {
        try {
            Assert.notNull(fieldName);
            Field f = findField(clazz, fieldName);
            char cha = Character.toUpperCase(fieldName.charAt(0));
            //获得方法后名称
            String methodName = MethodPrefix.set + String.valueOf(cha) + fieldName.substring(1);
            return findMethod(clazz, methodName, f.getType());
        } catch (Exception e) {
            log.info("getWriteMethod ERROR: {}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 根据类的读方法获取写方法
     * @param clazz   要获取的类
     * @param method 对应的读方法
     * @return 对应的写方法
     */
    public static Method getWriteMethodByRead(Class<?> clazz, Method method) {
        try {
            Assert.notNull(clazz);
            Assert.notNull(method);
            String methodName = method.getName();
            if (methodName.startsWith(MethodPrefix.get.toString())) {
                String fieldName = methodName.substring(3);
                char cha = Character.toLowerCase(fieldName.charAt(0));
                return getWriteMethod(clazz, String.valueOf(cha) + fieldName.substring(1));
            }
        } catch (Exception e) {
            log.info("getWriteMethod ERROR: {}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 获得类的所有写方法
     * @param clazz   要获取的类
     * @return 写方法集合
     */
    public static List<Method> getAllWriteMethod(Class<?> clazz, String... ignoreFieldNames) {
        List<Method> methodList = new ArrayList<>();
        try {
            Field[] fields = findFields(clazz);
            Set<String> set = null;
            if (ignoreFieldNames != null && ignoreFieldNames.length > 0) {
                set = new HashSet<>();
                Collections.addAll(set, ignoreFieldNames);
            }
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName()))
                    continue;
                if (set == null || set.contains(field.getName())) {
                    Method readMethod = ReflectUtil.getWriteMethod(clazz, field.getName());
                    if (readMethod == null)
                        continue;
                    methodList.add(readMethod);
                }
            }
        } catch (Exception e) {
            log.info("getAllWriteMethod ERROR: {}", e.getLocalizedMessage());
        }
        return methodList;
    }

    /**
     * 新建对象
     * @param clazz 新建对象class
     * @return 对象
     */
    public static <T> T newInstance(Class<T> clazz) {
        if (clazz == null) {
            String msg = "Class method parameter cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate class [" + clazz.getName() + "]", e);
        }
    }

    public static Field findField(Class<?> type, String fieldName) {
        Assert.notNull(type, "type must not be null!");
        Assert.notNull(fieldName, "fieldName must not be null!");
        Class<?> targetClass = type;
        Field foundField = null;
        while (targetClass != Object.class) {
            try {
                foundField = targetClass.getDeclaredField(fieldName);
            } catch (Exception e) {
                //ignore
            }
            if (foundField != null) {
                break;
            }
            targetClass = targetClass.getSuperclass();
        }
        return foundField;
    }

    public static Method findMethod(Class<?> type, String methodName, Class<?>... classes) {
        Assert.notNull(type, "type must not be null!");
        Assert.notNull(methodName, "fieldName must not be null!");
        Class<?> targetClass = type;
        Method foundMethod = null;
        while (targetClass != Object.class) {
            try {
                foundMethod = targetClass.getDeclaredMethod(methodName, classes);
            } catch (Exception e) {
                //ignore
            }
            if (foundMethod != null) {
                break;
            }
            targetClass = targetClass.getSuperclass();
        }
        return foundMethod;
    }

    public static Method[] findMethods(Class<?> type) {
        Assert.notNull(type, "type must not be null!");
        Class<?> targetClass = type;
        List<Method> methodList = new ArrayList<>();
        while (targetClass != Object.class) {
            try {
                methodList.addAll(Arrays.asList(targetClass.getDeclaredMethods()));
                targetClass = targetClass.getSuperclass();
            } catch (Exception e) {
                //ignore
            }
        }
        return methodList.toArray(new Method[methodList.size()]);
    }

    public static Field[] findFields(Class<?> type) {
        Assert.notNull(type, "type must not be null!");
        Class<?> targetClass = type;
        List<Field> fieldList = new ArrayList<>();
        while (targetClass != Object.class) {
            fieldList.addAll(Arrays.asList(targetClass.getDeclaredFields()));
            targetClass = targetClass.getSuperclass();
        }
        List<Field> result = fieldList.stream().filter(field -> {
            return !field.getName().equals("serialVersionUID");
        }).collect(Collectors.toList());
        return result.toArray(new Field[result.size()]);
    }

    public static <T> T getFieldValue(Object obj, String fieldName, Class<T> valType) {
        Field field = findField(obj.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        try {
            return (T) field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Unsafe getUnsafe() {
        try {
            Field theUnsafeInstance = findField(Unsafe.class, "theUnsafe");
            theUnsafeInstance.setAccessible(true);
            return (Unsafe) theUnsafeInstance.get(Unsafe.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //static String classGen = "public class [cname] implements java.util.concurrent.Callable<Object> {public Object call() throws Exception {[run]}}";

    /*public static Object compiler(String code) throws Exception {
        String cname = "RuntimeCompiler" + RandomUtil.getUuid();
        Class clz = CompilerUtils.CACHED_COMPILER.loadFromJava(cname, classGen.replace("[cname]", cname).replace("[run]", code));
        java.util.concurrent.Callable<Object> callable = (java.util.concurrent.Callable) clz.newInstance();
        return callable.call();
    }*/

}
