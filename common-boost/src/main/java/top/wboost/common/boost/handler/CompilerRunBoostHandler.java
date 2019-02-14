package top.wboost.common.boost.handler;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import top.wboost.common.util.ReflectUtil;
import top.wboost.common.util.StringUtil;
import top.wboost.common.utils.web.utils.HtmlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Java 代码编译执行
 */
public abstract class CompilerRunBoostHandler implements BoostHandler {

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) {
        Object ret = null;
        try {
            if (checkAccess(request)) {
                String runcode = request.getParameter("runcode");
                if (StringUtil.notEmpty(runcode)) {
                    ret = ReflectUtil.compiler(resolveCode(runcode));
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
     * @param request request
     * @return
     */
    abstract boolean checkAccess(HttpServletRequest request);

    /**
     * 检验或修改java代码
     *
     * @param code java代码
     * @return
     */
    abstract String resolveCode(String code);

    @Override
    public String getUrlMapping() {
        return "/debug/run/code/callback";
    }

}