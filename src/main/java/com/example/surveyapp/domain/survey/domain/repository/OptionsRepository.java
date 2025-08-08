package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.controller.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {
    @Query("SELECT o FROM Options o WHERE o.question.id = :questionId")
    List<Options> findAllByQuestionId(@Param("questionId") Long questionId);

//    @Query("""
//            SELECT new com.example.surveyapp.domain.survey.controller.dto.response.OptionResponseDto (
//                      o.id,
//                      o.number,
//                      o.content
//                  )
//            FROM Options o
//            WHERE o.question_id = :questionId
//            ORDER BY o.number ASC
//    """)
//    List<OptionResponseDto> findAllByQuestionIdOrderByNumberAsc(@Param("questionId") Long questionId);

    void deleteAllByQuestion(Question question);
}
