<<<<<<<< HEAD:src/main/java/com/example/surveyapp/domain/surveyanswer/domain/strategy/SingleChoiceAnswerStrategy.java
package com.example.surveyapp.domain.surveyanswer.domain.strategy;
========
package com.example.surveyapp.domain.survey.domain.strategy;
>>>>>>>> dev-v2-DDD:src/main/java/com/example/surveyapp/domain/survey/domain/strategy/SingleChoiceAnswerStrategy.java

import com.example.surveyapp.domain.surveyanswer.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyOptionsAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyOptionsAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleChoiceAnswerStrategy implements SurveyQuestionStrategy {
    private final SurveyOptionsAnswerRepository surveyOptionsAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Long questionId) {
        Number answer = (Number) questionAnswer.getAnswer();
        surveyOptionsAnswerRepository.save(new SurveyOptionsAnswer(surveyAnswer, questionId, answer.longValue()));
    }

    @Override
    public boolean isSupport(QuestionType questionType) {
        return questionType.isSingleChoice();
    }

}
