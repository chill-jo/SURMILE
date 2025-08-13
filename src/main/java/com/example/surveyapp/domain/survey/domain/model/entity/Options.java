package com.example.surveyapp.domain.survey.domain.model.entity;

import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "options" )
public class Options extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long number;

    @Column(nullable = false, length = 255)
    private String content;

    public Options(Long number, String content){
        this.number = number;
        this.content = content;
    }
    public void update(Long number, String content){
        if(number != null){ this.number = number; }
        if(content != null){ this.content = content; }
    }
}
