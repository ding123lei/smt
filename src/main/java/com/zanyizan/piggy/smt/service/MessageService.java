package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.Utils.CommonUtils;
import com.zanyizan.piggy.smt.common.enums.MessageType;
import com.zanyizan.piggy.smt.common.enums.StatusType;
import com.zanyizan.piggy.smt.common.enums.TaskStatus;
import com.zanyizan.piggy.smt.dao.MessageMapper;
import com.zanyizan.piggy.smt.entity.MessageEntity;
import com.zanyizan.piggy.smt.entity.response.GlanceVo;
import com.zanyizan.piggy.smt.entity.response.IndexVo;
import com.zanyizan.piggy.smt.entity.response.MeVo;
import com.zanyizan.piggy.smt.tables.records.ZyzMessageRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskFlowLogRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zanyizan.piggy.smt.Tables.ZYZ_MESSAGE;

/**
 * @author dinglei
 * @date 2018/07/19
 */
@Service
public class MessageService {

    @Autowired
    DSLContext dslContext;

    @Autowired
    TaskService taskService;

    public static final List<String> glanced = new ArrayList<>();
    public static final Map<String, String> notSavingMoney = new HashMap<>();
    public static final List<String> savedMoney = new ArrayList<>();
    public static String heIsTitle;
    public static final List<String> heIsMessage = new ArrayList<>();
    public static String heForYouTitle;
    public static String heForYouMessage;
    public static String savedWithMessage;
    public static String remarkSavedMoney;
    public static final List<String> remarkSingle = new ArrayList<>();
    public static final Map<String, String> remarkDoubleNotSavingMoney = new HashMap<>();

    private static final Random random = new Random();

    static{
        glanced.add("TA可能在偷看你存的钱");
        glanced.add("TA偷看了你3次，去看看TA");

        notSavingMoney.put("0,6", "有点早了，别打扰TA");
        notSavingMoney.put("6,12", "TA估计还没起床呢，叫醒TA");
        notSavingMoney.put("12,20", "TA还没有存钱，戳这里提醒");
        notSavingMoney.put("20,24", "TA还是要放弃，戳这里提醒");

        savedMoney.add("TA为你\"破\"了财，快看看吧");
        savedMoney.add("TA一定是藏了私房钱，快看");
        savedMoney.add("已经存钱，看看说了什么");
        savedMoney.add("TA一直很努力的在存钱");

        heIsTitle = "TA是";
        heIsMessage.add("再晚也要想着你，为了和你旅行");
        heIsMessage.add("每天都想着你，不曾间断");
        heIsMessage.add("为你存下我所有的私房钱");

        heForYouTitle = "TA为你";
        heForYouMessage = "存下%s元";

        savedWithMessage = "TA存了一笔钱，看看TA说了什么";

        remarkSingle.add("单身狗才自己存钱玩");
        remarkSingle.add("你的TA在哪里？");
        remarkSingle.add("你是不是把TA搞丢了？");
        remarkSingle.add("这里有一条单身\"猪\"");

        remarkSavedMoney = "明天来存钱看新风景";

        remarkDoubleNotSavingMoney.put("0,6", "还不睡呢");
        remarkDoubleNotSavingMoney.put("6,12", "来的真早");
        remarkDoubleNotSavingMoney.put("12,20", "想吃下午茶了");
        remarkDoubleNotSavingMoney.put("20,24", "亲早点睡");
    }

    @Autowired
    private MessageMapper messageMapper;

    public List<MessageEntity> getMessageList(String taskId, String userId, MessageType messageType, Date startDate, Date endDate){
        return messageMapper.getMessageList(taskId, userId, messageType == null ? null : messageType.getCode(), startDate, endDate);
    }

    public List<GlanceVo.GlanceMessage> glance(ZyzTaskRecord taskRecord, String userId, String friendUserId, Date startDate, Date endDate){
        String taskId = taskRecord.getTaskId();
        //插入偷看消息
        insertMessage(taskId, userId, friendUserId, "", MessageType.GLANCE);
        List<GlanceVo.GlanceMessage> messages = new ArrayList<>();
        //查看对方是否存钱
        List<ZyzTaskFlowLogRecord> taskFlowLogRecords = taskService.getTaskFlowLogs(taskId, friendUserId, startDate, endDate, true);
        if(taskFlowLogRecords != null && !taskFlowLogRecords.isEmpty()){
            messages.add(GlanceVo.GlanceMessage.builder().message(String.format(heForYouMessage, taskFlowLogRecords.get(0).getMoney())).title(heForYouTitle).build());
        }
        //查看对方偷看信息
        List<MessageEntity> messageEntities = getMessageList(taskId, userId, MessageType.GLANCE, startDate, endDate);
        if(messageEntities != null && messageEntities.size() != 0){
            messages.add(GlanceVo.GlanceMessage.builder().message(heIsMessage.get(random.nextInt(heIsMessage.size()))).title(heIsTitle).build());
        }
        return messages;
    }

    public int insertMessage(String taskId, String source, String target, String content, MessageType type){
        ZyzMessageRecord record = new ZyzMessageRecord();
        record.setTaskId(taskId);
        record.setSourceUid(source);
        record.setTargetUid(target);
        record.setContent(content);
        record.setType(type.getCode());
        return dslContext.insertInto(ZYZ_MESSAGE).set(record).execute();
    }

    public boolean needMessage(String taskId, String source, String target){
        ZyzMessageRecord zyzMessageRecord = dslContext.selectFrom(ZYZ_MESSAGE).
                where(ZYZ_MESSAGE.TASK_ID.eq(taskId).
                        and(ZYZ_MESSAGE.SOURCE_UID.eq(source)).
                        and(ZYZ_MESSAGE.TARGET_UID.eq(target))).
                        and(ZYZ_MESSAGE.TYPE.eq(MessageType.LEAVE_MESSAGE.getCode())).
                orderBy(ZYZ_MESSAGE.CREATE_TIME.desc()).limit(1).fetchOne();
        return zyzMessageRecord == null;
    }

    public String getRemark(ZyzTaskRecord taskRecord, String userId){
        List<ZyzTaskFlowLogRecord> flowLogRecords = taskService.getTaskFlowLogs(taskRecord.getTaskId(), userId, null, true);
        if(flowLogRecords == null || flowLogRecords.size() == 0){
            if(taskRecord.getStatus() == TaskStatus.SINGLE.ordinal()){
                return remarkSingle.get(random.nextInt(remarkSingle.size()));
            }else if(taskRecord.getStatus() == TaskStatus.SLEEP.ordinal() || taskRecord.getStatus() == TaskStatus.FINISH.ordinal()){
                return null;
            }
            return getMapValue(remarkDoubleNotSavingMoney);
        }else{
            return remarkSavedMoney;
        }
    }

    //获取首页右上角的friend status 最多两条
    public List<IndexVo.FriendStatus> getFriendStatus(ZyzTaskRecord taskRecord, String userId, String friendUserId){
        List<ZyzTaskFlowLogRecord> flowLogRecords = taskService.getTaskFlowLogs(taskRecord.getTaskId(), friendUserId, null, true);
        List<IndexVo.FriendStatus> list = new ArrayList<>();
        if(flowLogRecords == null || flowLogRecords.size() == 0){
            String message = getMapValue(notSavingMoney);
            list.add(IndexVo.FriendStatus.builder().message(message).type(StatusType.NOT_SAVE.getCode()).build());
        }else{
            list.add(IndexVo.FriendStatus.builder().message(savedMoney.get(random.nextInt(savedMoney.size()))).type(StatusType.SAVED.getCode()).build());
        }
        int todayGlanceCount = messageMapper.getCount(taskRecord.getTaskId(), userId, MessageType.GLANCE.getCode(), CommonUtils.parseDateTimeToDate(null));
        int allGlanceCount = messageMapper.getCount(taskRecord.getTaskId(), userId, MessageType.GLANCE.getCode(), CommonUtils.parseDateTimeToDate(taskRecord.getStartDate()));
        if(todayGlanceCount >= 1){
            list.add(IndexVo.FriendStatus.builder().message(glanced.get(0)).type(StatusType.SAVED.getCode()).build());
        }else{
            if(allGlanceCount >= 3){
                list.add(IndexVo.FriendStatus.builder()
                        .message(glanced.get(1))
                        .type((flowLogRecords == null || flowLogRecords.size() == 0) ? StatusType.NOT_SAVE.getCode() : StatusType.SAVED.getCode())
                        .build());
            }
        }
        return list;
    }

    public String getMapValue(Map<String, String> map){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Map.Entry<String, String> kvEntry = map.entrySet().stream().filter(entry -> {
            String[] hours = entry.getKey().split(",");
            int startHour = Integer.parseInt(hours[0]);
            int endHour = Integer.parseInt(hours[1]);
            if(hour >= startHour && hour <= endHour){
                return true;
            }
            return false;
        }).findFirst().orElse(null);
        return kvEntry == null ? null : kvEntry.getValue();
    }

    public MeVo.FriendStatus buildFriendStatus(MessageEntity entity){
        String time = CommonUtils.parseDateToString(entity.getCreateTime());
        String text;
        if(entity.getType() == MessageType.LEAVE_MESSAGE.getCode()){
            text = savedWithMessage;
        }else if(entity.getType() == MessageType.GLANCE.getCode()){
            text = glanced.get(0);
        }else {
            text = savedMoney.get(random.nextInt(savedMoney.size()));
        }
        return MeVo.FriendStatus.builder().
                time(time).
                ts(entity.getCreateTime().getTime()).
                text(text).
                type(StatusType.SAVED.getCode()).
                build();
    }

    //今天是否已经偷看过
    public boolean isRead(ZyzTaskRecord taskRecord, String userId, Date startDate, Date endDate){
        return messageMapper.isRead(taskRecord.getTaskId(), userId, null, startDate, endDate);
    }

    //标识为已读
    public void updateIsRead(ZyzTaskRecord taskRecord, String userId, Date startDate, Date endDate){
        messageMapper.updateIsRead(taskRecord.getTaskId(), userId, null, startDate, endDate);
    }
}
