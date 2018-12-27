package top.wboost.common.system.aop;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParamUtil {

    private static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 获得日志数据json
     * @param @param joinPoint
     * @param @return
     * @return String
     * @author jwSun
     * @date 2016年7月20日下午9:43:41
     */
    public static String getParamJson(JoinPoint joinPoint) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        try {
            MethodSignature methodSignature = ((MethodSignature) joinPoint.getSignature());
            Method domethod = methodSignature.getMethod();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(domethod);
            Object[] objects = joinPoint.getArgs();
            for (int i = 0; i < objects.length; i++) {
                Object obj = objects[i];
                if (!(obj == null || obj instanceof HttpSession || obj instanceof ServletRequest || obj instanceof ServletResponse
                        || obj instanceof org.springframework.web.multipart.commons.CommonsMultipartFile
                        || obj.getClass().getName()
                        .indexOf("org.springframework.web.multipart.commons.CommonsMultipartFile") != -1)) {
                    paramMap.put(parameterNames[i], obj);
                }
            }
            return JSONObject.toJSONString(paramMap);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * 获得方法自己的注解key value
     * 返回map   key:注解名_注解属性名  value:注解值
     * @param @param joinPoint
     * @param @return
     * @return Map<String,String>
     * @author jwSun
     * @date 2016年7月23日上午11:08:13
     */
    public static Map<String, Object> getAnnotationParams(JoinPoint joinPoint) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Method[] methods = joinPoint.getTarget().getClass().getDeclaredMethods();// 获得代理对象的目标对象所有方法
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {// 获得正在执行的方法
                Annotation[] annotations = method.getDeclaredAnnotations();// 获得正在执行方法的所有注解
                for (Annotation annotation : annotations) {
                    if ("org.springframework.web.bind.annotation.RequestMapping"
                            .equals(annotation.annotationType().getName())) {// 去除RequestMapping注解，留下自己的
                        continue;
                    }
                    for (Method AnnMethod : annotation.annotationType().getDeclaredMethods()) {// 获得注解的方法
                        String key = annotation.annotationType().getSimpleName() + "_" + AnnMethod.getName();
                        Object value = null;
                        try {
                            value = AnnMethod.invoke(annotation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //注解名_注解属性名:注解值
                        retMap.put(key, value);
                    }
                }
            }
        }
        return retMap;
    }

    /**
     * 获得方法自己的注解key value
     * 返回map   key:注解属性名  value:注解值
     * @param @param joinPoint
     * @param @return
     * @return Map<String,String>
     * @author jwSun
     * @date 2016年7月23日上午11:08:13
     */
    public static <T extends Annotation> Map<String, Object> getAnnotationParams(JoinPoint joinPoint,
            Class<T> annotationClass) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Method[] methods = joinPoint.getTarget().getClass().getDeclaredMethods();// 获得代理对象的目标对象所有方法
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {// 获得正在执行的方法
                Annotation annotation = method.getAnnotation(annotationClass);
                if (annotation != null) {
                    for (Method annMethod : annotation.annotationType().getDeclaredMethods()) {// 获得注解的方法
                        String key = annMethod.getName();
                        Object value = null;
                        try {
                            value = annMethod.invoke(annotation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //注解属性名:注解值
                        retMap.put(key, value);
                    }
                }
            }
        }
        return retMap;
    }

    /**
     * 获得当前方法中的除RequestMapping外所有注解的值
     * 返回map   key:方法名_注解名_注解属性名  value:注解值
     * @author sjw
     * @date 2016年7月21日 下午2:24:58
     */
    /*private static Map<String,String> getAnnotationParams(JoinPoint joinPoint) throws Exception{
    	Map<String,String> map = new HashMap<String,String>();
    	Method[] methods = joinPoint.getTarget().getClass().getMethods();
    	String thisMethod = joinPoint.getSignature().getName();
    	for(Method method:methods){
    		if(thisMethod.equals(method.getName())){
    			Annotation[] annotations = method.getAnnotations();
    			for(Annotation annotation:annotations){
    				for(Method annotationMethod:annotation.annotationType().getDeclaredMethods()){
    					//方法名_注解名_注解属性名:注解值
    					String key = method.getName()+"_" + annotation.annotationType().getSimpleName() + "_" + annotationMethod.getName();
    					if(key.indexOf("RequestMapping") == -1){
    						String value = "";
    						if(map.get(key)!=null){
    							value = map.get(key)+","+JSONObject.toJSONString(annotationMethod.invoke(annotation, null));
    						}else{
    							value = JSONObject.toJSONString(annotationMethod.invoke(annotation, null));
    						}
    						map.put(key,value);
    					}
    				}
    			}
    		}
    	}
    	return map;
    }*/

}
