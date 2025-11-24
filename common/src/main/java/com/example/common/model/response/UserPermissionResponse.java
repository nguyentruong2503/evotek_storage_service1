package com.example.common.model.response;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPermissionResponse {
    private String username;
    private List<String> roles;
    private List<String> permissions;
}
