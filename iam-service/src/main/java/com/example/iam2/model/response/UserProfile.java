package com.example.iam2.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserProfile {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String street;
    private String ward;
    private String district;
    private String province;
    private Integer yearsOfEx;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthday;
}
