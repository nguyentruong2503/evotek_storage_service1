package com.example.common.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Date birthday;
    private String street;
    private String ward;
    private String district;
    private String province;
    private Integer yearsOfEx;
    private Boolean locked;
    private Boolean deleted;
    Set<RoleDTO> roleDTOList;
}
