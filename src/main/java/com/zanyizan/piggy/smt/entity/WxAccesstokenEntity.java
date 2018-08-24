package com.zanyizan.piggy.smt.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@Data
@Builder
public class WxAccesstokenEntity {

    private String openId;
    private String sessionKey;
}
