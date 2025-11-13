package com.example.iam2.model.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserExcelDTO {
    private Integer stt;
    private String username;
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

    private List<String> errors = new ArrayList<>();
}
