package com.zanyizan.piggy.smt.entity.response;

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
public class AnswerVo {

    private String answerOption;
    private String answerCode;
}
