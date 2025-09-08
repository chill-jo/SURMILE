package com.example.surveyapp.domain.order.infrastructure;

import com.example.surveyapp.domain.order.application.ProductClient;
import com.example.surveyapp.domain.order.application.ProductClientResponseDto;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.JwtProviderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductWebClient implements ProductClient {
    private final WebClient.Builder webClientBuilder;
    private final JwtProviderImpl jwtProvider;

    @Override
    public ProductClientResponseDto getProduct(Long userId, Long productId){

        String token = jwtProvider.createServiceToken(userId, "order");
        ApiResponse<ProductClientResponseDto> response = webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/api/products/{id}", productId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ProductClientResponseDto>>(){})
                .block();

        return response.getData();
    }
}
