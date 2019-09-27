package top.wboost.common.boost.handler.compiler;

import com.alibaba.fastjson.JSONObject;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import top.wboost.common.boost.handler.BoostHandler;
import top.wboost.common.log.util.LoggerUtil;
import top.wboost.common.util.RandomUtil;
import top.wboost.common.util.ReflectUtil;
import top.wboost.common.util.StringUtil;
import top.wboost.common.utils.web.utils.HtmlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Java 代码编译执行
 */
public abstract class CompilerRunBoostHandler implements BoostHandler {

    private static final Logger LOGGER = LoggerUtil.getLogger(CompilerRunBoostHandler.class);
    static String methodGen = "public Object call() throws Exception {[run]}";
    static Object jarLauncher;
    static Object archives;
    static Method createClassLoader;

    public static ClassLoader createClassLoader() throws Exception {
        return (ClassLoader) createClassLoader.invoke(jarLauncher, archives);
    }

    public static Object compiler(String code) throws Exception {
        String cname = "RuntimeCompiler" + RandomUtil.getUuid();
        boolean is_boot_app = false;
        try {
            Class.forName("org.springframework.boot.loader.archive.Archive");
            is_boot_app = true;
        } catch (Exception e) {
            // ignore
        }
        if (is_boot_app) {
            try {
                jarLauncher = CompilerRunBoostHandler.class.getClassLoader().loadClass("org.springframework.boot.loader.JarLauncher").newInstance();
                Method getClassPathArchives = ReflectUtil.findMethod(jarLauncher.getClass(), "getClassPathArchives");
                getClassPathArchives.setAccessible(true);
                archives = getClassPathArchives.invoke(jarLauncher);
                createClassLoader = ReflectUtil.findMethod(jarLauncher.getClass(), "createClassLoader", List.class);
                createClassLoader.setAccessible(true);
                ClassPool.getDefault().appendClassPath(new SpringBootClassPath());
            } catch (Exception e) {
                LOGGER.warn("CompilerRunBoostHandler init error! Not a SpringBoot Application? ", e);
            }
        }
        CtClass ctClass = ClassPool.getDefault().get(CompilerTestClass.class.getName());
        ctClass.setName(cname);
        CtMethod make = CtNewMethod.make(methodGen.replace("[run]", code), ctClass);
        ctClass.addMethod(make);
        Class aClass = ctClass.toClass();
        return ReflectUtil.findMethod(aClass, "call").invoke(aClass.newInstance());
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) {
        Object ret = null;
        try {
            if (checkAccess(request)) {
                String runcode = request.getParameter("runcode");
                if (StringUtil.notEmpty(runcode)) {
                    ret = compiler(resolveCode(runcode));
                } else {
                    ret = "please use runcode parameter";
                }
            } else {
                ret = "NO ACCESS";
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = e.getMessage();
        }
        HtmlUtil.writerJson(response, JSONObject.toJSONString(ret));
        return null;
    }

    /**
     * 权限验证
     *
     * @param request request
     * @return
     */
    public abstract boolean checkAccess(HttpServletRequest request);

    /**
     * 检验或修改java代码
     *
     * @param code java代码
     * @return
     */
    public abstract String resolveCode(String code);

    @Override
    public String getUrlMapping() {
        return "/debug/run/code/callback";
    }

}