package com.example.iam2.model.dto;

import com.example.common.model.dto.RoleDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;
    List<RoleDTO> roleDTOList;
}
