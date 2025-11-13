package com.example.iam2.service;

import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.request.UserExcelDTO;
import com.example.iam2.model.request.UserExportRequest;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.model.response.UserDetail;
import com.example.iam2.model.response.UserProfile;
import org.springframework.security.core.GrantedAuthority;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface UserService {
    UserDTO create (UserDTO userDTO);
    UserProfile findUserById(String token);

    void lockUser(Long id);

    void unlockUser(Long id);

    void deleteUser(Long id);

    void resetPassword(Long id);

    PagedResponse<UserDTO> getAllUsers(int page, int size);

    UserDetail userDetail(Long id);

    void assignRoleToUser(AssignRoleDTO assignRoleDTO);

    //Lấy role và permission của user trong database (dùng trong keyloak)
    List<GrantedAuthority> getAuthoritiesByUsername(String username);

    //import excel
    List<UserExcelDTO> importUsers(InputStream excelFile) throws Exception;

    ByteArrayInputStream exportUsers(UserExportRequest request) throws IOException;
}
