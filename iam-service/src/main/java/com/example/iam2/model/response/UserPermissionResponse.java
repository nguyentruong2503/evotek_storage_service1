package com.example.iam2.model.response;

import java.util.List;

public record UserPermissionResponse(
        String username,
        List<String> roles,
        List<String> permissions
) {}
