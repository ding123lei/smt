package com.zanyizan.piggy.smt.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVo {

    private String questionId;
    private String question;
    private String taskCode;
    private String articleType;
    private List<AnswerVo> answers;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerVo{
        private String answerId;
        private String answer;
        private String answerOption;
    }
}
