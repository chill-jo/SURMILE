package com.example.surveyapp.domain.survey.domain.model.entity;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.SurveyUpdateRequestDto;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.global.config.entity.BaseEntity;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Embedded
    private SurveyInfo surveyInfo;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SurveyStatus status;

    @Column(name= "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "survey_id")
    private List<Question> questions = new ArrayList<>();

    public Survey(Long userId, SurveyInfo surveyInfo) {
        this.userId = userId;
        this.surveyInfo = surveyInfo;
        this.status = SurveyStatus.NOT_STARTED;
        this.isDeleted = false;
    }

    public static Survey of(Long userId, SurveyInfo surveyInfo) {
        return new Survey(userId, surveyInfo);
    }

    public void changeSurveyStatus(SurveyStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
    }

    public void updateSurveyInfo(SurveyUpdateRequestDto requestDto){
        surveyInfo = surveyInfo.updateSurveyInfo(requestDto);
    }

    public void deleteSurvey(){
        if(isDeleted){
            throw new CustomException(ErrorCode.SURVEY_ALREADY_DELETED);
        }
        isDeleted = true;
    }

    public void addQuestion(Question question){
        questions.add(question);
    }

    public Question updateQuestion(Long questionId, Long number, String content, QuestionType type){
        Question question = getQuestionById(questionId);
        question.update(number, content ,type);

        return question;
    }

    public Question getQuestionById(Long questionId){
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
    }

    public List<Question> getQuestions(){
        return questions.stream()
                .sorted(Comparator.comparing(Question::getNumber))
                .collect(Collectors.toList());
    }

    private void validateStatus(SurveyStatus newStatus){
        if(status.equals(newStatus)){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(newStatus.isNotStarted()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(status.isDone() && newStatus.isInProgress()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(!status.isInProgress() && newStatus.isPaused()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(status.isNotStarted() && newStatus.isDone()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
    }

    public boolean isNotStarted(){
        return status.equals(SurveyStatus.NOT_STARTED);
    }
    public boolean isInProgress(){
        return status.isInProgress();
    }

    public boolean isUserSurveyCreator(Long userId){
        return this.userId.equals(userId);
    }
}
