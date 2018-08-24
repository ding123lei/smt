package com.zanyizan.piggy.smt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dinglei
 * @date 2018/07/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {

    private String questionId;
    private String question;
    private String taskCode;
    private String articleType;
    private int sort;

    private String answerId;
    private String answer;
    private String answerOption;

    private int consume;
    private int deposit;
    private int manage;
    private int financing;
}
