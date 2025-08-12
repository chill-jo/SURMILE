package com.example.surveyapp.domain.survey.domain.service;

import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.exception.SurveyErrorCode;
import com.example.surveyapp.domain.survey.exception.SurveyException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyQuestionService {

    public void addQuestion(Survey survey, Question question){
        survey.getQuestions().add(question);
    }

    public Question updateQuestion(Survey survey, Long questionId, Long number, String content, QuestionType type){
        Question question = getQuestionById(survey, questionId);
        question.update(number, content ,type);

        return question;
    }

    public Question getQuestionById(Survey survey, Long questionId){
        return survey.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.QUESTION_NOT_FOUND));
    }

    public Question getQuestionByNumber(Survey survey, Long number){
        return survey.getQuestions().stream()
                .filter(q -> q.getNumber().equals(number))
                .findFirst()
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.QUESTION_NOT_FOUND));
    }

    public List<Question> getQuestionsSortedByNumber(Survey survey){
        return survey.getQuestions().stream()
                .sorted(Comparator.comparing(Question::getNumber))
                .collect(Collectors.toList());
    }

    public void deleteQuestionById(Survey survey, Long questionId){
        List<Question> questions = survey.getQuestions();
        questions.removeIf(q -> q.getId().equals(questionId));
    }



}
