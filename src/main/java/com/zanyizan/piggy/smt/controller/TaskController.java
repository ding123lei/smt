package com.zanyizan.piggy.smt.controller;

import com.zanyizan.piggy.smt.Utils.CommonUtils;
import com.zanyizan.piggy.smt.common.ApiResponse;
import com.zanyizan.piggy.smt.common.enums.ResponseCode;
import com.zanyizan.piggy.smt.common.enums.TaskStatus;
import com.zanyizan.piggy.smt.entity.response.ExpectVo;
import com.zanyizan.piggy.smt.entity.response.GlanceVo;
import com.zanyizan.piggy.smt.entity.response.SummaryVo;
import com.zanyizan.piggy.smt.service.MessageService;
import com.zanyizan.piggy.smt.service.QuestionService;
import com.zanyizan.piggy.smt.service.TaskService;
import com.zanyizan.piggy.smt.tables.pojos.ZyzUserTask;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskFlowLogRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/19
 */
@RestController
@RequestMapping("/deposit")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    MessageService messageService;

    @Autowired
    QuestionService questionService;

    @RequestMapping("/startNew.do")
    public ApiResponse<Boolean> startNew(@RequestParam("userId") String userId,
                                         @RequestParam("money") Integer money,
                                         @RequestParam(name = "days", required = false) Integer days){
        ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(userId);
        if(taskRecord != null){
            return ApiResponse.OK();
        }
        ZyzTaskRecord record = taskService.newTask(userId, money, days);
        if(record != null){
            return ApiResponse.OK();
        }
        return ApiResponse.FAIL();
    }

    @RequestMapping("/accept.do")
    public ApiResponse<Boolean> accept(@RequestParam("userId") String userId,
                                         @RequestParam("inviteUserId") String inviteUserId){
        ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(inviteUserId);
        if(taskRecord == null){
            return new ApiResponse<>(ResponseCode.NO_DATA);
        }
        if(taskRecord.getStatus() == TaskStatus.DOUBLE.ordinal()){
            return new ApiResponse<>(ResponseCode.ALREADY_HAVE_COMPANION);
        }
        ZyzTaskRecord taskRecord1 = taskService.getTaskByUserWithoutInit(userId);
        if(taskRecord1 != null){
            if(taskRecord1.getStatus() == TaskStatus.DOUBLE.ordinal()){
                return new ApiResponse<>(ResponseCode.ALREADY_HAVE_COMPANION);
            }else{
                taskRecord1.setStatus(TaskStatus.FINISH.ordinal());
                taskService.updateTask(taskRecord1, userId);
            }
        }
        taskService.joinTask(taskRecord.getTaskId(), userId);
        return ApiResponse.OK();
    }

    @RequestMapping("/glance.do")
    public ApiResponse<GlanceVo> glance(@RequestParam("userId") String userId,
                                        @RequestParam(name = "time", required = false) Long time){
        if(time == null){
            time = System.currentTimeMillis();
        }
        ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(userId);
        if(taskRecord == null){
            return new ApiResponse<>(ResponseCode.NO_DATA);
        }
        if(taskRecord.getStatus() == TaskStatus.FINISH.ordinal()){
            return new ApiResponse<>(ResponseCode.ALREADY_FINISH);
        }
        ZyzUserTask userTaskRecord = taskService.getFriendByTaskId(taskRecord.getTaskId(), userId);
        if(userTaskRecord == null){
            return new ApiResponse<>(ResponseCode.NO_COMPANION);
        }
        Date startDate = new Date(time);
        startDate = CommonUtils.parseDateTimeToDate(startDate);
        Date endDate = new Date(time + 24 * 60 * 60 * 1000);
        endDate = CommonUtils.parseDateTimeToDate(endDate);
        boolean glanced = messageService.isRead(taskRecord, userId, startDate, endDate);
        List<GlanceVo.GlanceMessage> messages = messageService.glance(taskRecord, userId, userTaskRecord.getUserId(), startDate, endDate);
        GlanceVo glanceVo = GlanceVo.builder().message(messages).glanced(glanced).build();
        return new ApiResponse<>(glanceVo);
    }

    @RequestMapping("/confirmGlance.do")
    public ApiResponse<?> confirmGlance(@RequestParam("userId") String userId,
                                        @RequestParam(name = "time", required = false) Long time){
        if(time == null){
            time = System.currentTimeMillis();
        }
        ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(userId);
        if(taskRecord == null){
            return new ApiResponse<>(ResponseCode.NO_DATA);
        }
        Date startDate = new Date(time);
        startDate = CommonUtils.parseDateTimeToDate(startDate);
        Date endDate = new Date(time + 24 * 60 * 60 * 1000);
        endDate = CommonUtils.parseDateTimeToDate(endDate);
        messageService.updateIsRead(taskRecord, userId, startDate, endDate);
        return ApiResponse.OK();
    }

    @RequestMapping("/savingMoney.do")
    public ApiResponse<?> savingMoney(@RequestParam("userId") String userId,
                                     @RequestParam("amount") Integer amount){
        ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(userId);
        if(taskRecord == null){
            return new ApiResponse<>(ResponseCode.NO_DATA);
        }
        if(taskRecord.getStatus() == TaskStatus.FINISH.ordinal()){
            return new ApiResponse<>(ResponseCode.ALREADY_FINISH);
        }
        List<ZyzTaskFlowLogRecord> list = taskService.getTaskFlowLogs(taskRecord.getTaskId(), userId, null , true);
        if(list != null && list.size() != 0){
            return new ApiResponse<>(ResponseCode.ALREADY_SAVING_MONEY_TODAY);
        }
        return new ApiResponse<>(taskService.saveMoney(taskRecord, userId, amount));
    }

    @RequestMapping("/help.do")
    public ApiResponse<?> help(@RequestParam("userId") String userId,
                               @RequestParam("targetUserId") String targetUserId){
        ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(targetUserId);
        if(taskRecord == null){
            return new ApiResponse<>(ResponseCode.NO_DATA);
        }
        if(taskRecord.getStatus() == TaskStatus.FINISH.ordinal()){
            return new ApiResponse<>(ResponseCode.SUCCESS);
        }
        if(taskRecord.getStatus() != TaskStatus.SLEEP.ordinal()){
            return new ApiResponse<>(ResponseCode.SUCCESS);
        }
        taskService.help(taskRecord, targetUserId);
        return ApiResponse.OK();
    }

    @RequestMapping("/expectDays.do")
    public ApiResponse<ExpectVo> expectDays(@RequestParam("userId") String userId,
                                     @RequestParam(value = "money", required = false) Integer money){
        ExpectVo expectVo = taskService.getExpectDays(userId, money);
        return new ApiResponse<>(expectVo);
    }

    @RequestMapping("/summary.do")
    public ApiResponse<SummaryVo> summary(@RequestParam("userId") String userId){
        boolean isOver = questionService.checkQuestionIsOver(userId);
        if(!isOver){
            return new ApiResponse<>(ResponseCode.ONE_OR_MORE_QUESTION_NEED_TO_ANSWER);
        }
        SummaryVo summaryVo = taskService.getSummary(userId);
        return new ApiResponse<>(summaryVo);
    }
}
