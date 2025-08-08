package com.example.surveyapp.domain.survey.service;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.service.ModerationService;
import com.example.surveyapp.domain.survey.controller.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.OptionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.OptionsRepository;
import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.facade.UserFacade;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionsService {

    private final OptionsRepository optionsRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final UserFacade userFacade;
    private final ModerationService moderationService;

    @Transactional
    public OptionResponseDto createOption(Long userId, Long surveyId, Long questionId, OptionCreateRequestDto requestDto){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        if(question.isSubjective()){
            throw new CustomException(ErrorCode.OPTION_INVALID_FOR_SUBJECTIVE_QUESTION);
        }

        currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        isSurveyNotStarted(survey);
        isQuestionFromSurvey(survey, question);
        validateOptionsModeration(requestDto.getContent());

        Options option = new Options(question, requestDto.getNumber(), requestDto.getContent());

        optionsRepository.save(option);

        return new OptionResponseDto(option.getId(), option.getNumber(), option.getContent());
    }

    //(inprogress일때는 모두, 다른 상태에는 관리자랑 해당 설문 출제자만 조회가능)
    //in progress이고 유저가 참여자권한인 경우 이미 참여했는지 확인해야함. - 응답 도메인 생성 후 수정
    public List<OptionResponseDto> getOptions(Long userId, Long surveyId, Long questionId){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        //진행 중 설문이 아닐 때는 참여자, 해당 설문 출제자가 아닌 출제자는 조회 불가.
        if(!survey.isInProgress()){
            isCurrentUserSurveyee(user);
            currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        }

        List<Options> optionsList = optionsRepository.findAllByQuestionId(questionId);

        List<OptionResponseDto> dtoList = optionsList.stream()
                .map(option -> new OptionResponseDto(option.getId(), option.getNumber(), option.getContent()))
                .collect(Collectors.toList());

        return dtoList;
    }

    @Transactional
    public OptionResponseDto updateOption(Long userId, Long surveyId, Long questionId, Long optionId, OptionUpdateRequestDto requestDto){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Options option = optionsRepository.findById(optionId).orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        isSurveyNotStarted(survey);
        isQuestionFromSurvey(survey, question);
        isOptionFromQuestion(question, option);
        validateOptionsModeration(requestDto.getContent());

        if(requestDto.getNumber() != null){
            option.changeNumber(requestDto.getNumber());
        }
        if(requestDto.getContent() != null){
            option.changeContent(requestDto.getContent());
        }

        return new OptionResponseDto(option.getId(), option.getNumber(), option.getContent());
    }

    @Transactional
    public void deleteOption(Long userId, Long surveyId, Long questionId, Long optionId){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Options option = optionsRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        isSurveyNotStarted(survey);
        isQuestionFromSurvey(survey, question);
        isOptionFromQuestion(question, option);

        optionsRepository.delete(option);

    }

    //참여자 권한 막기(생성, 수정, 삭제, 진행중아닌 설문선택지조회)
    public void isCurrentUserSurveyee(User user){

        if(user.isUserRoleSurveyee()){
            throw new CustomException(ErrorCode.SURVEYEE_NOT_ALLOWED);
        }
    }

    //해당 설문 출제자가 아니거나 관리자가 아닐 시 예외
    public void currentUserMatchesSurveyCreatorOrAdmin(User user, Survey survey){

        if(!survey.isUserSurveyCreator(user) && user.isUserRoleNotAdmin()){
            throw new CustomException(ErrorCode.NOT_SURVEY_CREATOR);
        }
    }

    //설문 상태가 진행 전이 아닐 때 생성, 수정, 삭제 시 예외
    public void isSurveyNotStarted(Survey survey){
        if(!survey.isNotStarted()){
            throw new CustomException(ErrorCode.SURVEY_STARTED);
        }
    }

    //설문에 포함된 질문이 아닐 때 예외
    public void isQuestionFromSurvey(Survey survey, Question question){
        if(!question.isFromSurvey(survey)){
            throw new CustomException(ErrorCode.QUESTION_NOT_FROM_SURVEY);
        }
    }

    //질문에 포함된 선택지가 아닐 때 예외
    public void isOptionFromQuestion(Question question, Options option){
        if(!option.isFromQuestion(question)){
            throw new CustomException(ErrorCode.OPTIONS_NOT_FROM_SURVEY);
        }
    }

    // 선택지 내용 적절성 검사
    private void validateOptionsModeration(String content) {
        ModerationResultStatusEnum titleStatus = moderationService.moderate("content", content);

        if(titleStatus == ModerationResultStatusEnum.DENIED){
            throw new CustomException(ErrorCode.INVALID_OPTION_CONTENT);
        }
    }
}
