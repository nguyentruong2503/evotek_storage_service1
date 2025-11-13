package com.example.iam2.controller;

import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.response.UserPermissionResponse;
import com.example.iam2.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iam")
public class IamController {
    @Autowired
    private IamService iamService;


    // Validate Keycloak token, trả về role/permission
    @GetMapping("/validate")
    public ResponseEntity<UserPermissionResponse> validateToken(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        UserPermissionResponse response = iamService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token")
    public UserDTO getUserByToken(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return iamService.getUserByToken(token);
    }

}
