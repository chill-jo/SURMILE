package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.AiModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.surveyanswer.application.facade.SurveyAnswerModerationFacade;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyAnswerModerationFacadeImpl implements SurveyAnswerModerationFacade {
    private final AiModerationService aiModerationService;
    private final UserReader userReader;
    @Override
    public AiModerationResult checkTextAnswerModeration(Long userId, String answer) {
        return aiModerationService.moderate(userId, userReader.emailById(userId), AiModerationTargetType.SURVEY_TEXT_ANSWER, answer);
    }
}
