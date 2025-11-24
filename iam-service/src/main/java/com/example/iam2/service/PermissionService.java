package com.example.iam2.service;

import com.example.iam2.model.dto.AssignPermissionDTO;
import com.example.iam2.model.dto.PermissionDTO;
import com.example.common.model.response.PagedResponse;

public interface PermissionService {
    PermissionDTO createPermission(PermissionDTO permissionDTO);

    PermissionDTO updatePermission(Long id,PermissionDTO permissionDTO);

    void deletePermission(Long id);

    PagedResponse<PermissionDTO> getAll(int page, int size);

    void assignPermissionToRole(AssignPermissionDTO assignPermissionDTO);
}
