package com.example.surveyapp.domain.survey.domain.model.entity;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.global.config.entity.BaseEntity;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    private Long surveyId;
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name = "question_id")
    private List<Options> options = new ArrayList<>();

    @Column(nullable = false)
    private Long number;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private QuestionType type;

    public Question(Long number, String content, QuestionType type){
        this.number = number;
        this.content = content;
        this.type = type;
    }

    public static Question of(Long number, String content, QuestionType type) {
        return new Question(number,content, type);
    }

    public static Question from(QuestionCreateRequestDto requestDto){
        return new Question(
                requestDto.getNumber(),
                requestDto.getContent(),
                requestDto.getType()
        );
    }

    public void update(Long number, String content, QuestionType type){
        this.number = number;
        this.content = content;
        this.type = type;

        if(this.type.equals(QuestionType.SUBJECTIVE)){
            options.clear();
        }
    }

    public void addOption(Options option){
        if(type.equals(QuestionType.SUBJECTIVE)){
            throw new CustomException(ErrorCode.OPTION_INVALID_FOR_SUBJECTIVE_QUESTION);
        }
        options.add(option);

    }

    public Options updateOption(Long optionId, Long number, String content){
        Options option = getOptionById(optionId);
        option.update(number, content);

        return option;
    }

    public Options getOptionById(Long optionId){
        return options.stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));
    }

    public List<Options> getOptionsOrderByNumber(){
        return options.stream()
                .sorted(Comparator.comparing(Options::getNumber))
                .collect(Collectors.toList());
    }

}
