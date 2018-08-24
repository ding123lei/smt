package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.Utils.CommonUtils;
import com.zanyizan.piggy.smt.Utils.SnowFlake;
import com.zanyizan.piggy.smt.common.enums.MessageType;
import com.zanyizan.piggy.smt.common.enums.TaskStatus;
import com.zanyizan.piggy.smt.dao.TaskMapper;
import com.zanyizan.piggy.smt.entity.SummaryEntity;
import com.zanyizan.piggy.smt.entity.UserFormIdEntity;
import com.zanyizan.piggy.smt.entity.WxTemplateKeyword;
import com.zanyizan.piggy.smt.entity.response.ExpectVo;
import com.zanyizan.piggy.smt.entity.response.SavingMoneyVo;
import com.zanyizan.piggy.smt.entity.response.SummaryVo;
import com.zanyizan.piggy.smt.tables.pojos.ZyzUserTask;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskFlowLogRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzUserRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzUserTaskRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static com.zanyizan.piggy.smt.Tables.*;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@Service
public class TaskService {

    @Value("${zyz.task.cycle:7}")
    private Integer taskCycle;

    @Value("${zyz.task.help-count:3}")
    private Integer helpCount;

    @Value("${zyz.money.sleepDueTime:3}")
    private Integer sleepDueTime;

    @Value("${zyz.message.savedMoneyNotice}")
    private String savedMoneyNotice;

    @Value("${zyz.message.expectedDays}")
    private String expectedDays;

    private final static String DEFAULT_TASK_PREFIX = "t_";

    @Autowired
    DSLContext dslContext;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    TemplateService templateService;

    @Autowired
    QuestionService questionService;

    @Autowired
    SnowFlake snowFlake;

    public ZyzTaskRecord getTaskByUserWithoutInit(String userId){
        ZyzTaskRecord record = taskMapper.getTaskByUserId(userId, TaskStatus.FINISH.ordinal());
        if(record != null && getSavingDays(record.getTaskId(), userId) > taskCycle){
            //task is over date
            record.setStatus(TaskStatus.FINISH.ordinal());
            updateTask(record, userId);
            return null;
        }
        return record;
    }

    public ZyzTaskRecord getTask(String userId){
        return taskMapper.getTaskByUserId(userId, TaskStatus.FINISH.ordinal());
    }

    public boolean haveTaskBefore(String userId){
        int count = taskMapper.userTaskCount(userId);
        return count > 0;
    }

    public void updateTask(ZyzTaskRecord record, String userId){
        //发送消息
        if(record.getStatus() == TaskStatus.FINISH.ordinal() && !StringUtils.isEmpty(userId)){
            //获取朋友用户信息
            UserFormIdEntity entity = userService.getUserFormIdEntityByFriendUserId(record.getTaskId(), userId);
            if(entity != null){
                if(!StringUtils.isEmpty(entity.getFormId()) && !StringUtils.isEmpty(entity.getThirdId())){
                    WxTemplateKeyword keyword = templateService.buildNewTaskNotice();
                    keyword.getKeyword2().setValue(String.valueOf(getTotalSavingDays(entity.getUserId())));
                    templateService.sendTemplate(entity.getThirdId(), entity.getFormId(), keyword);
                }
            }
        }
        dslContext.update(ZYZ_TASK)
                .set(ZYZ_TASK.STATUS, record.getStatus())
                .set(ZYZ_TASK.HELP_COUNT, record.getHelpCount())
                .set(ZYZ_TASK.DAYS, record.getDays())
                .set(ZYZ_TASK.TARGET_SUM, record.getTargetSum())
                .set(ZYZ_TASK.START_DATE, record.getStartDate())
                //.set(ZYZ_TASK.END_DATE, record.getEndDate())
                .where(ZYZ_TASK.TASK_ID.eq(record.getTaskId())).execute();
    }

    public ZyzUserTask getFriendByTaskId(String taskId, String userId){
        return taskMapper.getFriendByTaskId(taskId, userId);
    }

    public ZyzUserTaskRecord getFriendByUserId(String userId){
        return taskMapper.getFriendByUserId(userId);
    }

    public ZyzTaskRecord initTask(String userId){
        ZyzTaskRecord taskRecord = new ZyzTaskRecord();
        taskRecord.setDays(taskCycle);
        Date startDate = new Date(System.currentTimeMillis());
        taskRecord.setStartDate(startDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, taskCycle - 1);
        //taskRecord.setEndDate(new Date(calendar.getTimeInMillis()));
        taskRecord.setStatus(TaskStatus.SINGLE.ordinal());
        String taskId = DEFAULT_TASK_PREFIX + snowFlake.nextId();
        taskRecord.setTaskId(taskId);
        dslContext.insertInto(ZYZ_TASK).set(taskRecord).execute();

        ZyzUserTaskRecord userTaskRecord = new ZyzUserTaskRecord();
        userTaskRecord.setUserId(userId);
        userTaskRecord.setTaskId(taskId);
        dslContext.insertInto(ZYZ_USER_TASK).set(userTaskRecord).execute();

        return taskRecord;
    }

    public ZyzTaskRecord newTask(String userId, Integer money, Integer days){
        ZyzTaskRecord taskRecord = new ZyzTaskRecord();
        Date startDate = new Date(System.currentTimeMillis());
        taskRecord.setStartDate(startDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if(days == null){
            taskRecord.setDays(taskCycle);
            calendar.add(Calendar.DATE, taskCycle - 1);
        }else {
            taskRecord.setDays(days);
            calendar.add(Calendar.DATE, days - 1);
        }
        //taskRecord.setEndDate(new Date(calendar.getTimeInMillis()));
        taskRecord.setStatus(TaskStatus.SINGLE.ordinal());
        taskRecord.setTargetSum(money);
        String taskId = DEFAULT_TASK_PREFIX + snowFlake.nextId();
        taskRecord.setTaskId(taskId);
        dslContext.insertInto(ZYZ_TASK).set(taskRecord).execute();

        ZyzUserTaskRecord userTaskRecord = new ZyzUserTaskRecord();
        userTaskRecord.setUserId(userId);
        userTaskRecord.setTaskId(taskId);
        dslContext.insertInto(ZYZ_USER_TASK).set(userTaskRecord).execute();

        return taskRecord;
    }

    /*public List<IndexVo.FriendStatus> getDefaultMessageList(){
        List<IndexVo.FriendStatus> friendStatusList = new ArrayList<>();
        friendStatusList.add(IndexVo.FriendStatus.builder().
                type(StatusType.NOT_SAVE.getCode()).
                message(DEFAULT_NOT_SAVING_MESSAGE).
                build());
        friendStatusList.add(IndexVo.FriendStatus.builder().
                type(StatusType.NOT_SAVE.getCode()).
                message(DEFAULT_GLANCE_MESSAGE).
                build());
        return friendStatusList;
    }*/

    public boolean checkTaskIsSleep(ZyzTaskRecord record){
        if(CommonUtils.twoDaysDiffer(record.getStartDate(), null) < sleepDueTime){
            return false;
        }
        List<ZyzTaskFlowLogRecord> list = getTaskFlowLogs(record.getTaskId(), null, sleepDueTime, false);
        if(list != null && !list.isEmpty()){
            return false;
        }
        return true;
    }

    public List<ZyzTaskFlowLogRecord> getTaskFlowLogs(String taskId, String userId, Integer offSet, boolean ignoreZero){
        java.util.Date startDate = CommonUtils.parseDateTimeToDate(null);
        if(offSet != null && offSet != 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, - offSet);
            startDate = calendar.getTime();
        }
        return taskMapper.getTaskFlowLogs(taskId, userId, startDate, null, ignoreZero);
    }

    public List<ZyzTaskFlowLogRecord> getTaskFlowLogs(String taskId, String userId, java.util.Date startDate, java.util.Date endDate, boolean ignoreZero){
        return taskMapper.getTaskFlowLogs(taskId, userId, startDate, endDate, ignoreZero);
    }

    public Integer getTaskSavingMoneyTotal(String taskId, String userId){
        return taskMapper.getSavingAmount(taskId, userId);
    }

    public Integer getTaskSavingMoneyTotal(String taskId){
        return taskMapper.getSavingAmountAll(taskId);
    }

    public void joinTask(String taskId, String userId){
        dslContext.update(ZYZ_TASK).
                set(ZYZ_TASK.STATUS, TaskStatus.DOUBLE.ordinal()).
                where(ZYZ_TASK.TASK_ID.eq(taskId)).
                execute();
        ZyzUserTaskRecord userTaskRecord = new ZyzUserTaskRecord();
        userTaskRecord.setTaskId(taskId);
        userTaskRecord.setUserId(userId);
        dslContext.insertInto(ZYZ_USER_TASK).set(userTaskRecord).execute();
    }

    public SavingMoneyVo saveMoney(ZyzTaskRecord taskRecord, String userId, Integer money){
        ZyzUserTask userTask = this.getFriendByTaskId(taskRecord.getTaskId(), userId);
        if(userTask != null){
            messageService.insertMessage(taskRecord.getTaskId(), userId, userTask.getUserId(),
                    null, MessageType.SAVING_MONEY);
        }
        saveMoney(taskRecord.getTaskId(), userId, money);
        Integer amount = taskMapper.getSavingAmount(taskRecord.getTaskId(), userId);

        int days = getSavingDays(taskRecord.getTaskId(), userId);

        boolean needQuestion = questionService.needQuestion(taskRecord.getTaskId(), userId);
        boolean needMessage;
        if(userTask == null){
            needMessage = false;
        }else{
            //如果是第一天存钱 则显示留言 否则不显示留言
            needMessage = (days == 2);
        }
        boolean isFinish = false;
        boolean needSummary = false;
        if(days > taskCycle){
            taskRecord.setStatus(TaskStatus.FINISH.ordinal());
            updateTask(taskRecord, userId);
            isFinish = true;
            needSummary = taskMapper.needSummary(userId);
        }
        return SavingMoneyVo.builder().
                days(days).
                sum(amount).
                needQuestion(needQuestion).
                needMessage(needMessage).
                needSummary(needSummary).
                background(ImageService.getBackgroundImage(days)).
                subject(ImageService.getSubjectImage(taskRecord, userId, userTask, days)).
                isFinish(isFinish).
                notice(savedMoneyNotice).
                build();
    }

    private void saveMoney(String taskId, String userId, Integer money){
        ZyzTaskFlowLogRecord flowLogRecord = new ZyzTaskFlowLogRecord();
        flowLogRecord.setTaskId(taskId);
        flowLogRecord.setMoney(money);
        flowLogRecord.setUserId(userId);
        dslContext.insertInto(ZYZ_TASK_FLOW_LOG).set(flowLogRecord).execute();
    }

    public void help(ZyzTaskRecord taskRecord, String userId){
        taskRecord.setHelpCount((taskRecord.getHelpCount() == null ? 1 : taskRecord.getHelpCount()) + 1);
        if(this.helpCount <= taskRecord.getHelpCount()){
            ZyzUserTask userTask = getFriendByTaskId(taskRecord.getTaskId(), userId);
            TaskStatus status;
            if(userTask == null){
                status = TaskStatus.SINGLE;
            }else{
                status = TaskStatus.DOUBLE;
            }
            //存入0元 防止今天再次进入休眠状态
            this.saveMoney(taskRecord.getTaskId(), userId, 0);
            taskRecord.setHelpCount(0);
            taskRecord.setStatus(status.ordinal());
        }
        updateTask(taskRecord, null);
    }

    public int getSavingDays(String taskId, String userId){
        return taskMapper.getSavingDays(taskId, userId);
    }

    public int getTotalSavingDays(String userId){
        return taskMapper.getTotalSavingDays(userId);
    }

    public ExpectVo getExpectDays(String userId, Integer money){
        ZyzTaskRecord record = taskMapper.getLatestTaskByUserId(userId);
        int amount = getTaskSavingMoneyTotal(record.getTaskId());
        Double moneyPerDay = (double)amount / (double)taskCycle;
        moneyPerDay = moneyPerDay == 0 ? 1.0 : moneyPerDay;
        Integer days = null;
        if(money != null){
            Double d = (double)money / moneyPerDay;
            days = new Double(Math.ceil(d)).intValue();
        }
        String message = String.format(expectedDays, amount, moneyPerDay.intValue());
        return ExpectVo.builder()
                .days(days)
                .message(message)
                .build();
    }

    /**
     *
     * consume > 50 ZYZ55
     * deposit > 50 ZYZ50
     * manage > 30 ZYZ33
     * financing > 30 ZYZ30
     * other ZYZQT
     */
    public SummaryVo getSummary(String userId){
        SummaryEntity summaryEntity = taskMapper.getSummary(userId);
        taskMapper.insertSummary(summaryEntity);
        String summaryImage;
        String summaryCode;
        if(summaryEntity.getConsume() > 50){
            summaryImage = ImageService.getSummaryImage(1);
            summaryCode = ImageService.summaryMap.get(1);
        }else if(summaryEntity.getDeposit() > 50){
            summaryImage = ImageService.getSummaryImage(2);
            summaryCode = ImageService.summaryMap.get(2);
        }else if(summaryEntity.getManage() > 30){
            summaryImage = ImageService.getSummaryImage(3);
            summaryCode = ImageService.summaryMap.get(3);
        }else if(summaryEntity.getFinancing() > 30){
            summaryImage = ImageService.getSummaryImage(4);
            summaryCode = ImageService.summaryMap.get(4);
        }else{
            summaryImage = ImageService.getSummaryImage(5);
            summaryCode = ImageService.summaryMap.get(5);
        }
        return SummaryVo.builder().summaryImage(summaryImage).summaryCode(summaryCode).build();
    }

    public static void main(String[] args){
        int a = 5000;
        int b = 96;
        double c = (double) b / (double)7;
        Double d = (double) a / c;

        System.out.println(new Double(Math.ceil(d)).intValue());
    }
}
