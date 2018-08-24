package com.zanyizan.piggy.smt.controller;

import com.zanyizan.piggy.smt.common.ApiResponse;
import com.zanyizan.piggy.smt.common.enums.ResponseCode;
import com.zanyizan.piggy.smt.common.enums.TaskStatus;
import com.zanyizan.piggy.smt.entity.UserAnswer;
import com.zanyizan.piggy.smt.entity.response.AnswerVo;
import com.zanyizan.piggy.smt.entity.response.QuestionVo;
import com.zanyizan.piggy.smt.service.QuestionService;
import com.zanyizan.piggy.smt.service.TaskService;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author dinglei
 * @date 2018/07/28
 */
@RestController
@RequestMapping("/deposit")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/question.do", method = RequestMethod.POST)
    public ApiResponse<QuestionVo> getQuestion(@RequestParam String userId){
        ZyzTaskRecord zyzTaskRecord = taskService.getTaskByUserWithoutInit(userId);
        if(zyzTaskRecord == null){
            return new ApiResponse<>(ResponseCode.TASK_NOT_EXIST);
        }
        QuestionVo vo = questionService.getQuestion(userId);
        return new ApiResponse<>(vo);
    }

    @RequestMapping(value = "/answer.do", method = RequestMethod.POST)
    public ApiResponse<?> answer(@RequestParam String userId, @RequestParam String questionId, @RequestParam String answerId){
        ZyzTaskRecord zyzTaskRecord = taskService.getTaskByUserWithoutInit(userId);
        if(zyzTaskRecord == null){
            return new ApiResponse<>(ResponseCode.TASK_NOT_EXIST);
        }
        UserAnswer userAnswer = UserAnswer.builder().
                taskId(zyzTaskRecord.getTaskId()).
                userId(userId).
                questionId(questionId).
                answerId(answerId).
                build();
        Object obj = questionService.saveUserAnswer(userAnswer);
        if(obj == null){
            return ApiResponse.FAIL();
        }
        return new ApiResponse<>(obj);
    }

}
