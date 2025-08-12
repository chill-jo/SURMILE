package com.example.surveyapp.domain.user.domain.model;

import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "user_base_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBaseData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(nullable = false, length = 30)
    @Enumerated(value = EnumType.STRING)
    private CategoryEnum category;

    @Column(nullable = false)
    private Long data;

    private UserBaseData(User userId, CategoryEnum category, Long data) {
        this.userId = userId;
        this.category = category;
        this.data = data;
    }

    public static UserBaseData of(User userId, CategoryEnum category, Long data) {
        return new UserBaseData(userId, category, data);
    }

    public void update(Long data){
        this.data = data;
    }
}
