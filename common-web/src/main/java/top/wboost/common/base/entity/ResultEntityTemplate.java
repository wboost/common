package top.wboost.common.base.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class ResultEntityTemplate<T> {

    private T data;
    private ReturnInfoTemplate info = new ReturnInfoTemplate();
    private Boolean validate;
    private int status;

    @Data
    public static class ReturnInfoTemplate {
        private Integer code;
        private String message;
    }

    public static ResultEntityTemplate resolve(ResponseEntity<String> checkResponse) {
        if (checkResponse.getStatusCode() == HttpStatus.OK) {
            return JSONObject.parseObject(checkResponse.getBody(), ResultEntityTemplate.class);
        } else {
            throw new RuntimeException(checkResponse.toString());
        }
    }

    public boolean success() {
        return status == 0 && (validate == null || validate);
    }

}
