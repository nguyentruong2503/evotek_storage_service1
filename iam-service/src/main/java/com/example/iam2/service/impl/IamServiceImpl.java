package com.example.iam2.service.impl;

import com.example.iam2.converter.UserConverter;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.exception.NotFoundException;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.UserPermissionResponse;
import com.example.iam2.repository.UserRepository;
import com.example.iam2.service.IamService;
import com.example.iam2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IamServiceImpl implements IamService {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    @Override
    public UserPermissionResponse validateToken(String token) {
        // Decode JWT từ Keycloak
        Jwt jwt = NimbusJwtDecoder
                .withJwkSetUri("http://localhost:8080/realms/IAM2/protocol/openid-connect/certs")
                .build()
                .decode(token);

        List<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));

        String username = jwt.getClaimAsString("preferred_username");

        if (username != null) {
            try {
                authorities.addAll(userService.getAuthoritiesByUsername(username));
            } catch (Exception e) {
                System.out.println("Không thể lấy quyền từ DB: " + e.getMessage());
            }
        }

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .collect(Collectors.toList());

        List<String> permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList());


        return new UserPermissionResponse(username, roles, permissions);
    }

    @Override
    public UserDTO getUserByToken(String token) {
        Jwt jwt = NimbusJwtDecoder
                .withJwkSetUri("http://localhost:8080/realms/IAM2/protocol/openid-connect/certs")
                .build()
                .decode(token);

        List<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));

        String username = jwt.getClaimAsString("preferred_username");

        UserEntity entity = userRepository.findByUsernameAndLockedAndDeleted(username, false, false)
                .orElseThrow(() -> new NotFoundException("User không tồn tại hoặc đã bị khóa"));

        UserDTO dto = userConverter.toUserDTO(entity);
        return dto;
    }
}
