package com.zanyizan.piggy.smt.controller;

import com.zanyizan.piggy.smt.common.ApiResponse;
import com.zanyizan.piggy.smt.common.enums.MessageType;
import com.zanyizan.piggy.smt.common.enums.ResponseCode;
import com.zanyizan.piggy.smt.entity.response.IndexVo;
import com.zanyizan.piggy.smt.service.MessageService;
import com.zanyizan.piggy.smt.service.TaskService;
import com.zanyizan.piggy.smt.service.UserService;
import com.zanyizan.piggy.smt.tables.pojos.ZyzUserTask;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzUserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@RestController
@RequestMapping("/deposit")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    TaskService taskService;

    @RequestMapping("/leaveMessage.do")
    public ApiResponse<IndexVo> index(@RequestParam String userId,
                                      @RequestParam String message){
        Optional<ZyzUserRecord> optional = userService.getUser(userId);
        if(!optional.isPresent()){
            return new ApiResponse<>(ResponseCode.USER_NOT_EXIST);
        }
        ZyzTaskRecord record = taskService.getTaskByUserWithoutInit(userId);
        if(record == null){
            return new ApiResponse<>(ResponseCode.TASK_NOT_EXIST);
        }
        logger.info("user task : {}", record.toString());
        ZyzUserTask zyzUserTask = taskService.getFriendByTaskId(record.getTaskId(), userId);
        if(zyzUserTask == null){
            return new ApiResponse<>(ResponseCode.NO_COMPANION);
        }
        messageService.insertMessage(record.getTaskId(), userId, zyzUserTask.getUserId(), message, MessageType.LEAVE_MESSAGE);
        return new ApiResponse<>(ResponseCode.SUCCESS);
    }
}
