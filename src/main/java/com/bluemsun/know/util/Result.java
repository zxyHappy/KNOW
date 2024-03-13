package com.bluemsun.know.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success(String message, Object data) {
        Result commonResult = new Result();
        commonResult.code = 200;
        commonResult.message = message;
        commonResult.data = data;
        return commonResult;
    }

    public static Result error(Integer code, String message, Object data) {
        Result commonResult = new Result();
        commonResult.code = code;
        commonResult.message = message;
        commonResult.data = data;
        return commonResult;
    }

}
