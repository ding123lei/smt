package com.zanyizan.piggy.smt.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author dinglei
 * @date 2018/08/21
 */
@Mapper
public interface UserFormIdMapper {

    @Select("insert into zyz_user_formid (user_id, form_id) values (#{userId}, #{formId}) ON DUPLICATE KEY UPDATE form_id = #{formId} ")
    void upsertFormId(@Param("userId") String userId, @Param("formId") String formId);

    @Select("select form_id from zyz_user_formid where user_id = #{userId}")
    String getUserFormId(@Param("userId") String userId);
}
