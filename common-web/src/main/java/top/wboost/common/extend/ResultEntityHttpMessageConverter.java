package top.wboost.common.extend;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;
import top.wboost.common.base.annotation.AutoWebApplicationConfig;
import top.wboost.common.base.entity.ResultEntity;
import top.wboost.common.base.enums.CharsetEnum;
import top.wboost.common.constant.Global;
import top.wboost.common.util.ResponseUtil;
import top.wboost.common.utils.web.utils.HtmlUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * ResultEntity返回转换器
 * @className ResultEntityHttpMessageConverter
 * @author jwSun
 * @date 2017年7月4日 上午11:29:05
 * @version 1.0.0
 */
@AutoWebApplicationConfig("resultEntityHttpMessageConverter")
public class ResultEntityHttpMessageConverter extends AbstractHttpMessageConverter<ResultEntity> {

    /**默认编码-UTF-8**/
    public static final Charset DEFAULT_CHARSET = CharsetEnum.UTF_8.getCharset();

    /**使用编码**/
    private final Charset defaultCharset;

    public ResultEntityHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public ResultEntityHttpMessageConverter(Charset defaultCharset) {
        super();
        this.defaultCharset = defaultCharset;
    }
    
    protected boolean canWrite(MediaType mediaType) {
		return true;
	}

    @Override
    protected boolean supports(Class<?> clazz) {
        return ResultEntity.class == clazz;
    }

    @Override
    protected ResultEntity readInternal(Class<? extends ResultEntity> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(ResultEntity t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
        String result = null;
        boolean status = true;
        if (t != null) {
            result = ResponseUtil.codeResolveJson(t);
            if (t.getStatus() == 1) {
                Objects.requireNonNull(HtmlUtil.getResponse()).setStatus(Global.EXCEPTION_STATUS.value());
                status = false;
            } else if (t.getValidate() != null && !t.getValidate()) {
                Objects.requireNonNull(HtmlUtil.getResponse()).setStatus(HttpStatus.UNAUTHORIZED.value());
                status = false;
            }
        }
        if (status) {
            Objects.requireNonNull(HtmlUtil.getResponse()).addHeader("status", "0");
        } else {
            Objects.requireNonNull(HtmlUtil.getResponse()).addHeader("status", "1");
        }
        StreamUtils.copy(result, charset, outputMessage.getBody());
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        } else {
            return this.defaultCharset;
        }
    }

}