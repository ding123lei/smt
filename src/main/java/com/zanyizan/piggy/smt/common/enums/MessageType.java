package com.zanyizan.piggy.smt.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author dinglei
 * @date 2018/07/19
 */
public enum MessageType {

    SAVING_MONEY(1),
    GLANCE(2),
    LEAVE_MESSAGE(3),
    ;

    private int code;

    MessageType(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static MessageType getMessageTypeByCode(int code){
        Optional<MessageType> optional =  Arrays.stream(MessageType.values()).filter((type) ->
            type.getCode() == code
        ).findFirst();
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
