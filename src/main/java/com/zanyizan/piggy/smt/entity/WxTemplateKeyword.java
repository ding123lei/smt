package com.zanyizan.piggy.smt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dinglei
 * @date 2018/08/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxTemplateKeyword {

    //计划名
    private Value keyword1;
    //已打卡次数
    private Value keyword2;
    //提示语
    private Value keyword3;
    //打卡方式
    private Value keyword4;
    //目标
    private Value keyword5;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value{
        private String value;
    }
}
