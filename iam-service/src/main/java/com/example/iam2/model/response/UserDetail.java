package com.example.iam2.model.response;

import com.example.common.model.dto.RoleDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserDetail {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthday;
    private String street;
    private String ward;
    private String district;
    private String province;
    private Integer yearsOfEx;
    private Boolean locked;
    private Boolean deleted;
    private List<RoleDTO> roles;
}
