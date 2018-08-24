package com.zanyizan.piggy.smt.dao;

import com.zanyizan.piggy.smt.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @author dinglei
 * @date 2018/08/09
 */
@Mapper
public interface MessageMapper {

    @Select({"<script>" +
            "select * from zyz_message " +
            "where id in " +
            "(select max(id) from zyz_message " +
            "where task_id = #{taskId} and target_uid = #{uid} " +
            "<if test='messageType != null'>" +
            "AND type = #{messageType} " +
            "</if>" +
            "<if test='startDate != null'>" +
            "AND create_time <![CDATA[ >= ]]> #{startDate} " +
            "</if>" +
            "<if test='endDate != null'>" +
            "AND create_time <![CDATA[ <= ]]> #{endDate} " +
            "</if>" +
            "group by type,DATE_FORMAT(create_time,'%m/%d/%Y')) " +
            "order by create_time desc" +
            "</script>"})
    List<MessageEntity> getMessageList(@Param("taskId") String taskId,
                                       @Param("uid") String uid,
                                       @Param("messageType") Integer messageType,
                                       @Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate);
    @Select({"<script>" +
            "select count(1) from zyz_message " +
            "where " +
            "task_id = #{taskId} and target_uid = #{uid} " +
            "<if test='messageType != null'>" +
            "AND type = #{messageType} " +
            "</if>" +
            "<if test='date != null'>" +
            "AND create_time >= #{date} " +
            "</if>" +
            "</script>"})
    int getCount(@Param("taskId") String taskId,
                                       @Param("uid") String uid,
                                       @Param("messageType") Integer messageType,
                                       @Param("date") Date date);

    @Select({"<script>" +
            "select case when count(1) > 0 then true else false end as result from zyz_message " +
            "where " +
            "is_read = 1 and " +
            "task_id = #{taskId} and target_uid = #{uid} " +
            "<if test='messageType != null'>" +
            "AND type = #{messageType} " +
            "</if>" +
            "<if test='startDate != null'>" +
            "AND create_time <![CDATA[ >= ]]> #{startDate} " +
            "</if>" +
            "<if test='endDate != null'>" +
            "AND create_time <![CDATA[ <= ]]> #{endDate} " +
            "</if>" +
            "</script>"})
    boolean isRead(@Param("taskId") String taskId,
                                       @Param("uid") String uid,
                                       @Param("messageType") Integer messageType,
                                       @Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate);

    @Select({"<script>" +
            "update zyz_message " +
            "set " +
            "is_read = 1 " +
            "where " +
            "task_id = #{taskId} and target_uid = #{uid} " +
            "<if test='messageType != null'>" +
            "AND type = #{messageType} " +
            "</if>" +
            "<if test='startDate != null'>" +
            "AND create_time <![CDATA[ >= ]]> #{startDate} " +
            "</if>" +
            "<if test='endDate != null'>" +
            "AND create_time <![CDATA[ <= ]]> #{endDate} " +
            "</if>" +
            "</script>"})
    void updateIsRead(@Param("taskId") String taskId,
                         @Param("uid") String uid,
                         @Param("messageType") Integer messageType,
                         @Param("startDate") Date startDate,
                         @Param("endDate") Date endDate);
}
