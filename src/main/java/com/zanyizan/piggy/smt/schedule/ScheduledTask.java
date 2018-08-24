package com.zanyizan.piggy.smt.schedule;

import com.zanyizan.piggy.smt.entity.UserFormIdEntity;
import com.zanyizan.piggy.smt.entity.WxTemplateKeyword;
import com.zanyizan.piggy.smt.service.TaskService;
import com.zanyizan.piggy.smt.service.TemplateService;
import com.zanyizan.piggy.smt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/08/22
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    TemplateService templateService;

    @Scheduled(cron = "0 0 16 * * ?")
    protected void execute() {
        List<UserFormIdEntity> list = userService.getNotSavingMoneyUsers();
        if(list != null){
            list.stream().filter(entity -> {
                return !StringUtils.isEmpty(entity.getFormId()) && !StringUtils.isEmpty(entity.getThirdId());
            }).forEach(entity -> {
                //获取朋友用户信息
                if(!StringUtils.isEmpty(entity.getFormId()) && !StringUtils.isEmpty(entity.getThirdId())){
                    WxTemplateKeyword keyword = templateService.buildNormalNotice();
                    keyword.getKeyword2().setValue(String.valueOf(taskService.getTotalSavingDays(entity.getUserId())));
                    templateService.sendTemplate(entity.getThirdId(), entity.getFormId(), keyword);
                }
            });
        }
    }
}
