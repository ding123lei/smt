package com.zanyizan.piggy.smt.dao;

import com.zanyizan.piggy.smt.entity.UserFormIdEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/08/21
 */
@Mapper
public interface UserMapper {

    @Select("select u.user_id, u.third_id, uf.form_id from " +
            "zyz_user_task ut " +
            "left join " +
            "zyz_user u " +
            "on ut.user_id = u.user_id " +
            "left join " +
            "zyz_user_formid uf " +
            "on u.user_id = uf.user_id " +
            "where ut.task_id = #{taskId} and ut.user_id != #{userId}")
    UserFormIdEntity getUserFormIdEntityByFriendUserId(@Param("taskId") String taskId, @Param("userId") String userId);

    @Select("select uu.user_id,uu.third_id,uf.form_id from " +
            "zyz_user uu " +
            "left join " +
            "(select ut.user_id from " +
            "zyz_task_flow_log tf " +
            "left join " +
            "zyz_user_task ut " +
            "on tf.user_id = ut.user_id and tf.task_id = ut.task_id " +
            "left join " +
            "zyz_task t " +
            "on tf.task_id = t.task_id " +
            "where " +
            "tf.create_time > DATE_FORMAT(CURRENT_TIMESTAMP, '%Y-%m-%d') " +
            "and t.status != 3 group by ut.user_id) " +
            "tmp " +
            "on uu.user_id = tmp.user_id " +
            "left join " +
            "zyz_user_formid uf " +
            "on uu.user_id = uf.user_id " +
            "where " +
            "tmp.user_id is null")
    List<UserFormIdEntity> getNotSavingMoneyUsers();
}
