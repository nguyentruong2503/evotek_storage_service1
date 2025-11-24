package com.example.iam2.service;

import com.example.common.model.dto.UserDTO;
import com.example.common.model.response.UserPermissionResponse;

public interface IamService {
    UserPermissionResponse validateToken(String token);

    UserDTO getUserByToken(String token);
}
