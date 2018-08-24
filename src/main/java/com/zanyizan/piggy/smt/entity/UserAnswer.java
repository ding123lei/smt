package com.zanyizan.piggy.smt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author dinglei
 * @date 2018/07/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswer {

    private String userId;
    private String taskId;
    private String questionId;
    private String answerId;
    private Date createTime;

}
