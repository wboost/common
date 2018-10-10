package top.wboost.common.boost.handler;

import org.springframework.web.servlet.ModelAndView;
import top.wboost.common.annotation.Explain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BoostHandler {

    /**
     * 执行操作
     * @param request
     * @param response
     * @return
     */
    @Explain(value = "boost")
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获得执行的url
     * @return
     */
    public String getUrlMapping();

}
