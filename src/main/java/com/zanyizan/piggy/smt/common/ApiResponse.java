package com.zanyizan.piggy.smt.common;

import com.zanyizan.piggy.smt.common.enums.ResponseCode;
import lombok.Data;

/**
 * @author dinglei
 * @date 2018/07/17
 */
@Data
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public ApiResponse(T data){
        this(ResponseCode.SUCCESS);
        this.data = data;
    }

    public ApiResponse(ResponseCode responseCode){
        this.code = responseCode.code;
        this.message = responseCode.message;
    }

    public static <T> ApiResponse<T> OK(){
        return new ApiResponse<T>(ResponseCode.SUCCESS);
    }

    public static <T> ApiResponse<T> FAIL(){
        return new ApiResponse<T>(ResponseCode.FAIL);
    }
}
