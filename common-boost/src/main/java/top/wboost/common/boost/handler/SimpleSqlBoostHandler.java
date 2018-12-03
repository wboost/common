package top.wboost.common.boost.handler;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import top.wboost.common.util.StringUtil;
import top.wboost.common.utils.web.utils.HtmlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @AutoWebApplicationConfig("simpleSqlBoostHandler")
public class SimpleSqlBoostHandler extends AbstractSqlBoostHandler {

    @Override
    public ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response) {
        String sql = request.getParameter("sql");
        if (StringUtil.notEmpty(sql)) {
            HtmlUtil.writerJson(response, JSONObject.toJSONString(executeSql(null, sql)));
        } else {
            HtmlUtil.writerJson(response, "no sql");
        }
        return null;
    }

    @Override
    public String getUrlMapping() {
        return "/boost/sql";
    }

}
