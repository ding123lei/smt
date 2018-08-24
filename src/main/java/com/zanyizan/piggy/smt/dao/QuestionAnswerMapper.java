package com.zanyizan.piggy.smt.dao;

import com.zanyizan.piggy.smt.entity.QuestionAnswer;
import com.zanyizan.piggy.smt.entity.UserAnswer;
import com.zanyizan.piggy.smt.entity.response.QuestionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/29
 */
@Mapper
public interface QuestionAnswerMapper {

    @Select({"select q.question_id,q.question,q.task_code,q.article_type,a.answer_id,a.answer,m1.answer_option from " +
            "(select * from zyz_user_answer where user_id = #{userId} and task_id = #{taskId} order by create_time desc limit 1) ua " +
            "left join " +
            "zyz_question_answer_mapping m " +
            "on ua.question_id = m.question_id and ua.answer_id = m.answer_id " +
            "left join " +
            "zyz_question q " +
            "on m.next_question_id = q.question_id " +
            "left join " +
            "zyz_question_answer_mapping m1 " +
            "on q.question_id = m1.question_id " +
            "left join " +
            "zyz_answer a " +
            "on m1.answer_id = a.answer_id " +
            "order by m1.question_id,m1.answer_option asc"})
    QuestionVo getNextQuestionAndAnswer(@Param("taskId") String taskId, @Param("userId") String userId);

    @Select({"select q.question_id,q.question,q.task_code,q.article_type from " +
            "(select * from zyz_user_answer where user_id = #{userId} order by id desc limit 1) ua " +
            "left join " +
            "zyz_question_answer_mapping m " +
            "on ua.question_id = m.question_id and ua.answer_id = m.answer_id " +
            "left join " +
            "zyz_question q " +
            "on m.next_question_id = q.question_id "})
    QuestionVo getNextQuestion(@Param("userId") String userId);

    @Select({"select a.answer_id, a.answer ,m.answer_option from " +
            "zyz_question_answer_mapping m " +
            "left join " +
            "zyz_answer a " +
            "on m.answer_id = a.answer_id " +
            "where m.question_id = #{questionId} order by m.answer_option;"})
    List<QuestionVo.AnswerVo> getAnswerByQuestionId(@Param("questionId") String questionId);

    @Select({"select * from zyz_question where sort = 1 order by id limit 1"})
    QuestionVo getDefaultQuestion();

    @Select({"insert into zyz_user_answer (user_id, task_id, question_id, answer_id) value (#{userId}, #{taskId}, #{questionId}, #{answerId})"})
    void insertUserAnswer(UserAnswer answer);

    @Select({"select q.question_id,q.question,q.task_code,q.article_type,q.sort,a.answer_id,a.answer,m.answer_option,m.consume,m.deposit,m.manage,m.financing from " +
            "zyz_question_answer_mapping m " +
            "left join " +
            "zyz_question q " +
            "on m.question_id = q.question_id " +
            "left join " +
            "zyz_answer a " +
            "on a.answer_id = m.answer_id " +
            "where m.question_id = #{questionId} and m.answer_id = #{answerId}"})
    QuestionAnswer questionAnswer(@Param("questionId") String questionId, @Param("answerId") String answerId);

    /*@Select({"select create_time from zyz_user_answer where user_id = #{userId} and task_id = #{taskId} order by id desc limit 1"})
    Date lastestAnswerDate(@Param("taskId") String taskId, @Param("userId") String userId);*/

    @Select({"select * from zyz_user_answer where user_id = #{userId} order by id asc"})
    List<UserAnswer> selectUserAnswers(@Param("userId") String userId);
}
