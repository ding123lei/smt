package com.zanyizan.piggy.smt.dao;

import com.zanyizan.piggy.smt.entity.SummaryEntity;
import com.zanyizan.piggy.smt.entity.UserFormIdEntity;
import com.zanyizan.piggy.smt.tables.pojos.ZyzUserTask;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskFlowLogRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzUserTaskRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Mapper
public interface TaskMapper {

    @Select("select t.* from " +
            "zyz_task t left join zyz_user_task ut on " +
            "t.task_id = ut.task_id " +
            "where t.status != #{status} " +
            "and ut.user_id = #{userId} " +
            "order by t.create_time desc limit 1")
    ZyzTaskRecord getTaskByUserId(@Param("userId") String userId, @Param("status") int status);

    @Select("select t.* from " +
            "zyz_task t left join zyz_user_task ut on " +
            "t.task_id = ut.task_id " +
            "and ut.user_id = #{userId} " +
            "order by t.update_time desc limit 1")
    ZyzTaskRecord getLatestTaskByUserId(@Param("userId") String userId);

    @Select("select count(1) from " +
            "zyz_user_task t where t.user_id = #{userId}")
    int userTaskCount(@Param("userId") String userId);

    @Select("select t2.* from " +
            "zyz_user_task t1 left join zyz_user_task t2 on t1.task_id = t2.task_id " +
            "left join " +
            "zyz_task task on t1.task_id = task.task_id " +
            "left join " +
            "zyz_user u on t2.user_id = u.user_id " +
            "where t1.user_id != t2.user_id and t1.user_id = #{userId} and task.status != 3")
    ZyzUserTaskRecord getFriendByUserId(@Param("userId") String userId);

    @Select("select t.* from " +
            "zyz_user_task t left join zyz_task tt on " +
            "t.task_id = tt.task_id " +
            "where t.task_id = #{taskId} and t.user_id != #{userId} and tt.task_id != 3")
    ZyzUserTask getFriendByTaskId(@Param("taskId") String taskId, @Param("userId") String userId);

    @Select({"<script>" +
            "select t.* from " +
            "zyz_task_flow_log t left join zyz_task tt on t.task_id = tt.task_id " +
            "where t.task_id = #{taskId} " +
            "and tt.status != 3" +
            "<if test='userId != null'>" +
            "AND t.user_id = #{userId} " +
            "</if>" +
            "<if test='ignoreZero == true'>" +
            "AND t.money > 0 " +
            "</if>" +
            "<if test='startDate != null'>" +
            "AND t.create_time <![CDATA[ >= ]]> #{startDate} " +
            "</if>" +
            "<if test='endDate != null'>" +
            "AND t.create_time <![CDATA[ <= ]]> #{endDate} " +
            "</if>" +
            "order by t.create_time desc" +
            "</script>"})
    List<ZyzTaskFlowLogRecord> getTaskFlowLogs(@Param("taskId") String taskId, @Param("userId") String userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("ignoreZero") boolean ignoreZero);

    @Select("select sum(money) from zyz_task_flow_log where task_id = #{taskId} and user_id = #{userId}")
    Integer getSavingAmount(@Param("taskId") String taskId, @Param("userId") String userId);

    @Select("select sum(money) from zyz_task_flow_log where task_id = #{taskId}")
    Integer getSavingAmountAll(@Param("taskId") String taskId);

    @Select("select count(distinct(DATE_FORMAT(create_time,'%Y-%m-%d'))) + 1 as total from zyz_task_flow_log where task_id = #{taskId} and user_id = #{userId}")
    int getSavingDays(@Param("taskId") String taskId, @Param("userId") String userId);

    @Select("select count(distinct(DATE_FORMAT(create_time,'%Y-%m-%d'))) + 1 as total from zyz_task_flow_log where user_id = #{userId}")
    int getTotalSavingDays(@Param("userId") String userId);

    @Select("select ua.user_id,sum(consume) as consume,sum(deposit) as deposit,sum(manage) as manage,sum(financing) as financing from " +
            "zyz_user_answer ua " +
            "left join " +
            "zyz_question_answer_mapping m " +
            "on " +
            "ua.question_id = m.question_id and ua.answer_id = m.answer_id " +
            "where user_id = #{userId}")
    SummaryEntity getSummary(@Param("userId") String userId);

    @Select("insert into zyz_summary (user_id, consume, deposit, manage, financing) values (#{entity.userId}, #{entity.consume}, #{entity.deposit}, #{entity.manage}, #{entity.financing})")
    void insertSummary(@Param("entity") SummaryEntity entity);

    @Select("select case when count(1) > 0 then false else true end from " +
            "zyz_summary " +
            "where user_id = #{userId}")
    boolean needSummary(@Param("userId") String userId);
}
