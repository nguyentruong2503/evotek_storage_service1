package com.example.storage.model.response;

import lombok.Data;
import java.util.List;

@Data
public class UserPermissionResponse {
    private String username;
    private List<String> roles;
    private List<String> permissions;
}
