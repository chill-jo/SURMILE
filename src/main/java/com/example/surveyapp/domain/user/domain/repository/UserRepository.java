package com.example.surveyapp.domain.user.domain.repository;

import com.example.surveyapp.domain.admin.controller.dto.UserDto;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    boolean existsByIdAndIsDeletedFalse(Long userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query(value = """
        SELECT new com.example.surveyapp.domain.admin.controller.dto.UserDto(
                      u.id,
                      u.email,
                      u.name,
                      u.nickname,
                      u.userRole,
                      u.isDeleted
                  )
        FROM User u
        WHERE (:searchText IS NULL 
               OR u.email LIKE CONCAT('%', :searchText, '%')
               OR u.name LIKE CONCAT('%', :searchText, '%')
               OR u.nickname LIKE CONCAT('%', :searchText, '%'))
    """)
    Page<UserDto> findAllBySearch(@Param("searchText") String search, Pageable pageable);


}
