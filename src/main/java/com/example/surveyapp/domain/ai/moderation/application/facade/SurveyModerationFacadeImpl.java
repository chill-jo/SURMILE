package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.AiModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.survey.application.facade.SurveyModerationFacade;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyModerationFacadeImpl implements SurveyModerationFacade {
    private final AiModerationService aiModerationService;
    private final UserReader userReader;

    @Override
    public AiModerationResult checkTitleModeration(Long userId, String title) {
        return aiModerationService.moderate(userId, userReader.emailById(userId), AiModerationTargetType.SURVEY_TITLE, title);
    }

    @Override
    public AiModerationResult checkDescriptionModeration(Long userId, String description){
        return aiModerationService.moderate(userId, userReader.emailById(userId), AiModerationTargetType.SURVEY_DESCRIPTION, description);
    }

    @Override
    public AiModerationResult checkQuestionModeration(Long userId, String question) {
        return aiModerationService.moderate(userId, userReader.emailById(userId), AiModerationTargetType.SURVEY_QUESTION, question);
    }

    @Override
    public AiModerationResult checkOptionsModeration(Long userId, String options) {
        return aiModerationService.moderate(userId, userReader.emailById(userId), AiModerationTargetType.SURVEY_OPTIONS, options);
    }
}
