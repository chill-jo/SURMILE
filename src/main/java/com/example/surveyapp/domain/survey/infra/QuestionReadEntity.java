package com.example.surveyapp.domain.survey.infra;

import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "question")
@Immutable
public class QuestionReadEntity {

    @Id
    private Long id;

    private Long surveyId;

    private Long number;
    private String content;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    public Question toQuestion(){
        return new Question(number, content, type);
    }
}
