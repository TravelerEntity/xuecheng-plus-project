package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * Created by Lee Yian on 2024/4/2 16:20
 * 统一异常返回类
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
