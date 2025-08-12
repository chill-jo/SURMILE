package com.example.surveyapp.domain.admin.presentation;

import com.example.surveyapp.domain.admin.presentation.dto.StatsListDto;
import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
import com.example.surveyapp.domain.admin.application.AdminService;
import com.example.surveyapp.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 전체 회원 조회(검색)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUserList(
            @RequestParam(required = false) String search,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success("전체 회원 조회되었습니다.", adminService.getUserList(search, pageable)));
    }

    // 단일 회원 조회
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success("회원 조회되었습니다.", adminService.getUser(userId)));
    }

    // 분류별 참여자 통계 조회
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<StatsListDto>>> getStats(
            @RequestParam(required = false, defaultValue = "1999-01-01 00:00:00") String startDate,
            @RequestParam(required = false, defaultValue = "2999-01-01 00:00:00") String endDate
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateLocal = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateLocal = LocalDateTime.parse(endDate, formatter);

        return ResponseEntity.ok(ApiResponse.success("분류별 참여자 통계 조회하였습니다.", adminService.getStats(startDateLocal, endDateLocal)));

    }

    // 블랙리스트 등록
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/black/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> addBlackList(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success("블랙리스트에 등록되었습니다.", adminService.addBlackList(userId)));
    }


    // 블랙리스트 삭제
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/black/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> deleteBlackList(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success("블랙리스트에서 삭제되었습니다.", adminService.deleteBlackList(userId)));
    }
}
