package com.example.iam2.service;

import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.UserPermissionResponse;

public interface IamService {
    UserPermissionResponse validateToken(String token);

    UserDTO getUserByToken(String token);
}
