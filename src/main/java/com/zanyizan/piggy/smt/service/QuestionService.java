package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.Utils.CommonUtils;
import com.zanyizan.piggy.smt.dao.QuestionAnswerMapper;
import com.zanyizan.piggy.smt.entity.QuestionAnswer;
import com.zanyizan.piggy.smt.entity.UserAnswer;
import com.zanyizan.piggy.smt.entity.response.AnswerVo;
import com.zanyizan.piggy.smt.entity.response.QuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author dinglei
 * @date 2018/07/29
 */
@Service
public class QuestionService {

    private static final String ANSWER_MESSAGE = "关注公众号查看%s选项答案, 回复\"%s\"";

    @Autowired
    QuestionAnswerMapper questionAnswerMapper;

    public QuestionVo getQuestion(String userId){
        QuestionVo questionVo = questionAnswerMapper.getNextQuestion(userId);
        if(questionVo == null){
            questionVo = questionAnswerMapper.getDefaultQuestion();
        }
        List<QuestionVo.AnswerVo> answerVoList = questionAnswerMapper.getAnswerByQuestionId(questionVo.getQuestionId());
        questionVo.setAnswers(answerVoList);
        return questionVo;
    }

    public Object saveUserAnswer(UserAnswer userAnswer){
        QuestionAnswer questionAnswer = questionAnswerMapper.questionAnswer(userAnswer.getQuestionId(), userAnswer.getAnswerId());
        if (questionAnswer == null) {
            return null;
        }
        questionAnswerMapper.insertUserAnswer(userAnswer);
        if(questionAnswer.getSort() == 1){
            return getQuestion(userAnswer.getUserId());
        }
        return AnswerVo.builder().
                answerOption(questionAnswer.getAnswerOption()).
                answerCode(questionAnswer.getTaskCode()).
                build();
    }

    public boolean needQuestion(String taskId, String userId){
        List<UserAnswer> userAnswers = questionAnswerMapper.selectUserAnswers(userId);
        if(userAnswers == null || userAnswers.size() == 0){
            return true;
        }
        if(userAnswers.size() >= 7){
            return false;
        }
        int diff = CommonUtils.twoDaysDiffer(userAnswers.get(userAnswers.size() - 1).getCreateTime(), null);
        return diff > 0;
    }

    public boolean checkQuestionIsOver(String userId){
        QuestionVo questionVo = questionAnswerMapper.getNextQuestion(userId);
        return questionVo == null;
    }
}
