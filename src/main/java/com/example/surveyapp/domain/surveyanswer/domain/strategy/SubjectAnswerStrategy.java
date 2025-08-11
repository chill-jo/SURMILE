<<<<<<<< HEAD:src/main/java/com/example/surveyapp/domain/surveyanswer/domain/strategy/SubjectAnswerStrategy.java
package com.example.surveyapp.domain.surveyanswer.domain.strategy;
========
package com.example.surveyapp.domain.survey.domain.strategy;
>>>>>>>> dev-v2-DDD:src/main/java/com/example/surveyapp/domain/survey/domain/strategy/SubjectAnswerStrategy.java

import com.example.surveyapp.domain.surveyanswer.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyTextAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyTextAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubjectAnswerStrategy implements SurveyQuestionStrategy {
    private final SurveyTextAnswerRepository surveyTextAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Long questionId) {
        surveyTextAnswerRepository.save(new SurveyTextAnswer(surveyAnswer.getId(), questionId, (String) questionAnswer.getAnswer()));
    }

    @Override
    public boolean isSupport(QuestionType questionType){
        return questionType.isSubjective();
    }

}
