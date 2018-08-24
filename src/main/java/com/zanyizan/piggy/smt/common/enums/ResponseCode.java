package com.zanyizan.piggy.smt.common.enums;

public enum ResponseCode {

    SUCCESS(0, "success"),
    FAIL(-1, "fail"),
    BAD_PARAMS(101, "bad params"),
    NO_DATA(102, "no data"),
    USER_NOT_EXIST(103, "用户不存在"),
    ALREADY_HAVE_COMPANION(104, "无法接受，已参与计划"),
    NO_COMPANION(105, "没有同伴"),
    ALREADY_FINISH(106, "任务已结束"),
    TASK_NOT_EXIST(107, "任务不存在"),
    ALREADY_SAVING_MONEY_TODAY(108, "已存钱，明天来玩"),
    ONE_OR_MORE_QUESTION_NEED_TO_ANSWER(109, "还有问题未回答"),
    INTERNAL_ERROR(500, "internal error"),
    ;

    public int code;
    public String message;

    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }

}
