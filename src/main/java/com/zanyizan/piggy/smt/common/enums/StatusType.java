package com.zanyizan.piggy.smt.common.enums;

/**
 * @author dinglei
 * @date 2018/08/10
 */
public enum StatusType {

    NOT_SAVE(1),
    SAVED(2);

    private int code;

    StatusType(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
