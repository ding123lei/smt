package com.zanyizan.piggy.smt.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingMoneyVo {

    private int sum;
    private int days;
    private boolean needQuestion;
    private boolean needMessage;
    private boolean needSummary;
    private String background;
    private String subject;
    private boolean isFinish;
    private String notice;
}
