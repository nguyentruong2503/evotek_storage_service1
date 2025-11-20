package com.example.iam2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class KeycloakClientCredentialsService {

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Autowired
    private WebClient webClient;

    public String getAccessToken() {
       return webClient.post()
               .uri(tokenUri)
               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
               .body(BodyInserters
                       .fromFormData("grant_type","client_credentials")
                       .with("client_id", clientId)
                       .with("client_secret", clientSecret)
               )
               .retrieve()
               .bodyToMono(Map.class)
               .map(res -> (String) res.get("access_token"))
               .block();
    }
}

