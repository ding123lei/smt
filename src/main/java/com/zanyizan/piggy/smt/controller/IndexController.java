package com.zanyizan.piggy.smt.controller;

import com.zanyizan.piggy.smt.common.ApiResponse;
import com.zanyizan.piggy.smt.common.enums.ResponseCode;
import com.zanyizan.piggy.smt.common.enums.TaskStatus;
import com.zanyizan.piggy.smt.entity.response.IndexVo;
import com.zanyizan.piggy.smt.service.*;
import com.zanyizan.piggy.smt.tables.pojos.ZyzUserTask;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzUserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@RestController
@RequestMapping("/deposit")
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    MoneyService moneyService;

    @Autowired
    MessageService messageService;

    @RequestMapping("/index.do")
    public ApiResponse<IndexVo> index(@RequestParam String userId){
        Optional<ZyzUserRecord> optional = userService.getUser(userId);
        if(!optional.isPresent()){
            return new ApiResponse<>(ResponseCode.USER_NOT_EXIST);
        }
        ZyzTaskRecord record = taskService.getTaskByUserWithoutInit(userId);
        if(record == null){
            if(taskService.haveTaskBefore(userId)){
                IndexVo defaultIndexVo = getDefaultIndex(optional.get());
                return new ApiResponse<>(defaultIndexVo);
            }else{
                record = taskService.initTask(userId);
            }
        }
        logger.info("user task : {}", record.toString());
        boolean isSleep = taskService.checkTaskIsSleep(record);
        if(isSleep && record.getStatus() != TaskStatus.SLEEP.ordinal()){
            record.setStatus(TaskStatus.SLEEP.ordinal());
            record.setHelpCount(0);
            taskService.updateTask(record, null);
        }

        ZyzUserTask userTaskRecord = taskService.getFriendByTaskId(record.getTaskId(), userId);
        String friendAvatar = null;
        String friendUserId = null;
        List<IndexVo.FriendStatus> friendStatusList = null;
        if(userTaskRecord != null){
            friendUserId = userTaskRecord.getUserId();
            Optional<ZyzUserRecord> friendOptional = userService.getUser(userTaskRecord.getUserId());
            if(friendOptional.isPresent()){
                friendAvatar = friendOptional.get().getAvatar();
            }
            friendStatusList = messageService.getFriendStatus(record, userId, friendUserId);
        }
        int days = taskService.getSavingDays(record.getTaskId(), userId);
        int totalDays = taskService.getTotalSavingDays(userId);
        List<Integer> money = moneyService.getDailyMoney();
        IndexVo.Image image = IndexVo.Image.builder().
                avatar(UserService.getAvatar(optional.get())).
                friendAvatar(friendAvatar).
                background(ImageService.getBackgroundImage(days)).
                subject(ImageService.getSubjectImage(record, userId, userTaskRecord, days)).
                share(ImageService.getShareImage(record, userId, userTaskRecord, days)).
                imageCode(days).
                build();
        IndexVo vo = IndexVo.builder().
                image(image).
                userId(userId).
                friendUserId(userTaskRecord != null ? userTaskRecord.getUserId() : null).
                friendStatus(friendStatusList).
                remark(messageService.getRemark(record, userId)).
                status(record.getStatus()).
                money(money).
                totalDays(totalDays).
                build();
        return new ApiResponse<>(vo);
    }

    private IndexVo getDefaultIndex(ZyzUserRecord record){
        IndexVo.Image image = IndexVo.Image.builder().
                avatar(UserService.getAvatar(record)).
                background(ImageService.getDefaultBackgroundImage()).
                subject(ImageService.getDefaultSubjectImage()).
                share(ImageService.getDefaultShareImage()).
                imageCode(1).
                build();
        return IndexVo.builder().
                image(image).
                userId(record.getUserId()).
                remark(null).
                status(TaskStatus.FINISH.ordinal()).
                //friendStatus(taskService.getDefaultMessageList()).
                build();
    }
}
