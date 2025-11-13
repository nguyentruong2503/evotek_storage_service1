package com.example.storage.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.Map;

@Configuration
public class FeignClientConfig {

    @Value("${iam.client-id}")
    private String clientId;

    @Value("${iam.client-secret}")
    private String clientSecret;

    @Value("${iam.token-url}")
    private String tokenUrl;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Lấy token từ IAM (Client Credentials)
            String token = fetchAccessToken();
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

    private String fetchAccessToken() {
        WebClient webClient = WebClient.builder().build();

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .retrieve()
                .bodyToMono(Map.class)
                .map(m -> (String) m.get("access_token"))
                .block();
    }
}