package com.example.surveyapp.domain.product.presentation;

import com.example.surveyapp.domain.product.presentation.dto.ProductCreateRequestDto;
import com.example.surveyapp.domain.product.presentation.dto.ProductCreateResponseDto;
import com.example.surveyapp.domain.product.presentation.dto.ProductResponseDto;
import com.example.surveyapp.domain.product.presentation.dto.ProductUpdateRequestDto;
import com.example.surveyapp.domain.product.application.ProductService;
import com.example.surveyapp.domain.product.application.dto.ProductUpdateResponseDto;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> create(
            @Valid @RequestBody ProductCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        ProductCreateResponseDto product =  productService.createProduct(dto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("상품이 생성되었습니다.",product));
    }

    /**
     * 상품 전체 조회
     * 참여자,관리자만 조회 가능
     * 출제자는 상품 자체 조회 불가능
     */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYEE')")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getReadAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        List<ProductResponseDto> products = productService.readAllProduct(page,size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("전체 상품을 조회하였습니다.",products));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYEE')")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getReadOne(@PathVariable Long id) {
       ProductResponseDto product = productService.readOneProduct(id);
       return ResponseEntity.status(HttpStatus.OK)
               .body(ApiResponse.success("상품을 조회하였습니다.",product));
    }

    /**
     * 추후에 ADMIN 계정 확인 예외 처리도 해야함
     * data 값 받기
     * @param id
     * @param requestDto
     * @return
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductUpdateResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequestDto requestDto){
        ProductUpdateResponseDto responseDto = productService.updateProduct(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
               .body(ApiResponse.success("상품이 수정 되었습니다.",responseDto));
    }

    /**
     * 삭제
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();
        productService.deleteProduct(id,userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success( "상품이 삭제 됐습니다.",null));
    }
}
