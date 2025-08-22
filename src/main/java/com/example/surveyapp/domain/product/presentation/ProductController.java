package com.example.surveyapp.domain.product.presentation;

import com.example.surveyapp.domain.product.presentation.dto.*;
import com.example.surveyapp.domain.product.application.ProductService;
import com.example.surveyapp.domain.product.application.dto.ProductUpdateResponseDto;
import com.example.surveyapp.global.security.jwt.CustomSecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> create(
            @Valid @RequestBody ProductCreateRequestDto dto,
            @AuthenticationPrincipal CustomSecurityUserDetails userDetails) {
        Long userId = userDetails.getId();
        ProductCreateResponseDto product =  productService.createProduct(dto,userId);
        URI location = URI.create("/api/products" + product.getId());
        return ResponseEntity.created(location).body(product);
    }

    /**
     * 상품 전체 조회
     * 참여자,관리자만 조회 가능
     * 출제자는 상품 자체 조회 불가능
     */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYEE')")
    public ResponseEntity<List<ProductResponseDto>> getReadAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        List<ProductResponseDto> products = productService.readAllProduct(page,size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(products);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYEE')")
    public ResponseEntity<ProductResponseDto> getReadOne(@PathVariable Long id) {
       ProductResponseDto product = productService.readOneProduct(id);
       return ResponseEntity.status(HttpStatus.OK)
               .body(product);
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
    public ResponseEntity<ProductUpdateResponseDto> update(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequestDto requestDto){
        ProductUpdateResponseDto responseDto = productService.updateProduct(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
               .body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductStatusUpdateResponseDto> statusUpdate(@PathVariable Long id,
                                                                       @RequestBody ProductStatusUpdateRequestDto requestDto,
                                                                       @AuthenticationPrincipal CustomSecurityUserDetails userDetails) {
        Long userId = userDetails.getId();
        ProductStatusUpdateResponseDto responseDto = productService.statusUpdate(userId,id, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 삭제
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductUpdateResponseDto> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomSecurityUserDetails userDetails){
        Long userId = userDetails.getId();
        productService.deleteProduct(id,userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
