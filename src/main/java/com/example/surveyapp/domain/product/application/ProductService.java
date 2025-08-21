package com.example.surveyapp.domain.product.application;

import com.example.surveyapp.domain.product.exception.ProductErrorCode;
import com.example.surveyapp.domain.product.exception.ProductException;
import com.example.surveyapp.domain.product.presentation.dto.*;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.ProductPoints;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.domain.repository.ProductRepository;
import com.example.surveyapp.domain.product.application.dto.ProductUpdateResponseDto;
import com.example.surveyapp.global.oauth.reader.OauthReader;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final OauthReader oauthReader;

    /**
     * @param requestDto         생성 요청 DTO
     * @param userId 인증된 사용자 정보 가져오기(관리자만)
     * @return
     * @PreAuthorize("hashRole('ADMIN')") ADMIN 관리자만 생성 할 수 있도록
     */
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto, Long userId) {
        oauthReader.validateUserIdOrThrow(userId);
        oauthReader.validateUserRoleToAdmin(userId);

        if (productRepository.existsByTitleAndIsDeletedFalse(requestDto.getTitle())){
            throw new ProductException(ProductErrorCode.NOT_SAME_CREATE_PRODUCT_TITLE);
        }

        Product product = Product.of(requestDto.getTitle(),
                ProductPoints.of(requestDto.getPrice()),
                requestDto.getContent(),
                requestDto.getStatus());

        Product saved = productRepository.save(product);
        return ProductCreateResponseDto.from(saved);

    }

    /**
     *
     * @param page
     * @param size
     * @return
     */
    public List<ProductResponseDto> readAllProduct(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.findAllByStatusAndIsDeletedFalse(Status.ON_SALE,pageable);

        List<Product> productList = products.getContent();

        return productList.stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    public ProductResponseDto readOneProduct(Long id) {

        Product product= productRepository.findByIdAndStatusAndIsDeletedFalse(id,Status.ON_SALE)
                .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND_PRODUCT));
        return ProductResponseDto.from(product);
    }

    @Transactional
    public ProductUpdateResponseDto updateProduct(Long id, ProductUpdateRequestDto requestDto) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND_PRODUCT));

        product.changeStatus(product.getStatus());

        if (!product.getTitle().equals(requestDto.getTitle())) {
            boolean onlyOne = productRepository.existsByTitleAndIsDeletedFalse(requestDto.getTitle());
            if (onlyOne){
                throw new ProductException(ProductErrorCode.NOT_SAME_PRODUCT_TITLE);
            }

        }
         product.update(
                requestDto.getTitle(),
                ProductPoints.of(requestDto.getPrice()),
                requestDto.getContent(),
                requestDto.getStatus());

        product.changeStatus(requestDto.getStatus());

        return ProductUpdateResponseDto.from(product);

    }

    @Transactional
    public void deleteProduct(Long id, Long userId) {
        oauthReader.validateUserIdOrThrow(userId);
        oauthReader.validateUserRoleToAdmin(userId);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND_PRODUCT));

        product.delete();
    }

    @Transactional
    public ProductStatusUpdateResponseDto statusUpdate(Long userId, Long id, ProductStatusUpdateRequestDto requestDto) {
        oauthReader.validateUserIdOrThrow(userId);
        oauthReader.validateUserRoleToAdmin(userId);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND_PRODUCT));
        product.changeStatus(requestDto.getNewStatus());
        return ProductStatusUpdateResponseDto.from(product);
    }
}