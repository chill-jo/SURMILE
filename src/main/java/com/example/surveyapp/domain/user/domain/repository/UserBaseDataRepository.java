package com.example.surveyapp.domain.user.domain.repository;

import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserBaseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserBaseDataRepository extends JpaRepository<UserBaseData, Long> {

    Optional<UserBaseData> findByUserIdAndCategory(User userId, CategoryEnum category);

    List<UserBaseData> findAllByUserId(User userId);

    @Query("""
            SELECT COUNT(ubd)
            FROM UserBaseData AS ubd
            WHERE ubd.category = :category
                    AND ubd.data = :data
                    AND (ubd.updatedAt >= :startDate OR :startDate IS NULL)
                    AND (ubd.updatedAt <= :endDate OR :endDate IS NULL)
            """)
    Long countByCategoryAndDataAndStartDateAndEndDate(
            CategoryEnum category, Long data,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
