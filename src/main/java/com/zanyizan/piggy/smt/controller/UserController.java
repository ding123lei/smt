package com.zanyizan.piggy.smt.controller;

import com.zanyizan.piggy.smt.Utils.CommonUtils;
import com.zanyizan.piggy.smt.Utils.WxUtils;
import com.zanyizan.piggy.smt.common.ApiResponse;
import com.zanyizan.piggy.smt.common.enums.ResponseCode;
import com.zanyizan.piggy.smt.common.enums.StatusType;
import com.zanyizan.piggy.smt.entity.MessageEntity;
import com.zanyizan.piggy.smt.entity.WxUserInfoEntity;
import com.zanyizan.piggy.smt.entity.response.MeVo;
import com.zanyizan.piggy.smt.entity.response.UserVo;
import com.zanyizan.piggy.smt.service.MessageService;
import com.zanyizan.piggy.smt.service.TaskService;
import com.zanyizan.piggy.smt.service.UserService;
import com.zanyizan.piggy.smt.tables.records.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dinglei
 * @date 2018/07/17
 */
@RestController
@RequestMapping("/deposit")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    MessageService messageService;

    @ResponseBody
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public ApiResponse<UserVo> login(@RequestParam(name = "jsCode") String jsCode) {
        try {
            Optional<String> wxOptional = WxUtils.getOpenId(jsCode);
            if(wxOptional.isPresent()){
                Optional<UserVo> optional = userService.login(wxOptional.get());
                if(optional.isPresent()){
                    return new ApiResponse<>(optional.get());
                }
            }
        } catch (Exception e) {
            logger.info("UserController login failed, message : {}", e.getMessage());
            e.printStackTrace();
        }
        return ApiResponse.FAIL();
    }

    @ResponseBody
    @RequestMapping(value = "/me.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MeVo> me(@RequestBody WxUserInfoEntity entity) {
        try {
            if(!entity.check()){
                return new ApiResponse<>(ResponseCode.BAD_PARAMS);
            }
            Optional<ZyzUserRecord> userRecordOptional = userService.getUser(entity.getUserId());
            if(!userRecordOptional.isPresent()){
                return new ApiResponse<>(ResponseCode.USER_NOT_EXIST);
            }
            logger.info("UserController.me userRecord : {}", userRecordOptional.get());
            userService.update(entity);
            ZyzTaskRecord taskRecord = taskService.getTaskByUserWithoutInit(entity.getUserId());
            if(taskRecord == null){
                return new ApiResponse<>(ResponseCode.NO_DATA);
            }
            logger.info("UserController.me taskRecord : {}", taskRecord);
            MeVo meVo = new MeVo();
            ZyzUserRecord userRecord = userRecordOptional.get();
            Integer total = taskService.getTaskSavingMoneyTotal(taskRecord.getTaskId(), userRecord.getUserId());
            UserVo user = UserVo.builder().
                    avatar(UserService.getAvatar(userRecord)).
                    userId(userRecord.getUserId()).
                    userName(userRecord.getUserName()).
                    money(String.valueOf(total == null ? 0 : total)).
                    build();
            logger.info("UserController.me userVo : {}", user);
            meVo.setUser(user);
            ZyzUserTaskRecord userTaskRecord = taskService.getFriendByUserId(entity.getUserId());
            UserVo friend = null;
            List<MeVo.FriendStatus> list = new ArrayList<>();
            if(userTaskRecord != null){
                logger.info("UserController.me userTaskRecord : {}", userTaskRecord);
                Optional<ZyzUserRecord> friendRecordOptional = userService.getUser(userTaskRecord.getUserId());
                if(friendRecordOptional.isPresent()){
                    logger.info("UserController.me friendRecord : {}", friendRecordOptional.get());
                    ZyzUserRecord friendRecord = friendRecordOptional.get();
                    //是否偷看过
                    boolean glanced = messageService.isRead(taskRecord, entity.getUserId(), CommonUtils.parseDateTimeToDate(null), null);
                    meVo.setGlanced(glanced);
                    Integer friendTotal = taskService.getTaskSavingMoneyTotal(taskRecord.getTaskId(), friendRecord.getUserId());
                    friend = UserVo.builder().
                            avatar(UserService.getAvatar(friendRecord)).
                            userId(friendRecord.getUserId()).
                            userName(friendRecord.getUserName()).
                            money(glanced ? String.valueOf(friendTotal == null ? 0 : friendTotal) : "?").
                            build();
                    meVo.setFriend(friend);
                    logger.info("UserController.me friend : {}", friend);
                    //查看朋友今天是否存钱
                    List<ZyzTaskFlowLogRecord> taskFlowLogRecords = taskService.getTaskFlowLogs(taskRecord.getTaskId(), friendRecord.getUserId(), null, true);
                    logger.info("UserController.me taskFlowLogRecords : {}", taskFlowLogRecords);
                    if(taskFlowLogRecords == null || taskFlowLogRecords.isEmpty()){
                        Date date = new Date();
                        String time = CommonUtils.parseDateToString(date);
                        list.add(MeVo.FriendStatus.builder().
                                    time(time).
                                    ts(date.getTime()).
                                    text(messageService.getMapValue(MessageService.notSavingMoney)).
                                    type(StatusType.NOT_SAVE.getCode()).
                                    build());
                    }
                }
            }
            List<MessageEntity> messageEntities = messageService.getMessageList(taskRecord.getTaskId(), entity.getUserId(), null, null, null);
            if(messageEntities != null){
                logger.info("UserController.me messageEntities : {}", messageEntities);
                //有存钱或者偷看消息
                list.addAll(messageEntities.stream().map((record) -> {
                    return messageService.buildFriendStatus(record);
                }).collect(Collectors.toList()));
                meVo.setFriendStatus(list);
            }
            logger.info("UserController.me meVo : {}", meVo);
            return new ApiResponse<>(meVo);
        } catch (Exception e) {
            logger.info("UserController me failed, message : {}", e.getMessage());
            e.printStackTrace();
        }
        return ApiResponse.FAIL();
    }

    @ResponseBody
    @RequestMapping(value = "/acceptFormId.do", method = RequestMethod.POST)
    public ApiResponse<?> acceptFormId(@RequestParam(name = "userId") String userId,
                                            @RequestParam(name = "formId") String formId) {
        userService.upsertFromId(userId, formId);
        return ApiResponse.OK();
    }

}
