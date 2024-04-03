package com.xuecheng.base.exception;

/**
 * Created by Lee Yian on 2024/4/2 16:15
 * 通用异常类
 */
public enum CommonEx {

    UNKNOWN_ERROR("执行过程异常，请重试。"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");

    private final String exMessage;

    public String getExMessage() {
        return exMessage;
    }

    CommonEx(String exMessage) {
        this.exMessage = exMessage;
    }
}
