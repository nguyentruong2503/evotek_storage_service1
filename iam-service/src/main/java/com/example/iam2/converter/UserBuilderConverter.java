package com.example.iam2.converter;

import com.example.iam2.builder.UserBuilder;
import com.example.iam2.model.request.UserExportRequest;
import com.example.iam2.util.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class UserBuilderConverter {
    public UserBuilder toUserBuilder (UserExportRequest userExportRequest, List<String> roles){
        UserBuilder userBuilder = new UserBuilder.Builder()
                .setUsername(MapUtils.getObject(userExportRequest.getUsername(),String.class))
                .setEmail(MapUtils.getObject(userExportRequest.getEmail(), String.class))
                .setFirstName(MapUtils.getObject(userExportRequest.getFirstName(), String.class))
                .setLastName(MapUtils.getObject(userExportRequest.getLastName(), String.class))
                .setPhone(MapUtils.getObject(userExportRequest.getPhone(), String.class))
                .setBirthday(MapUtils.getObject(userExportRequest.getBirthday(), Date.class))
                .setStreet(MapUtils.getObject(userExportRequest.getStreet(), String.class))
                .setWard(MapUtils.getObject(userExportRequest.getWard(), String.class))
                .setDistrict(MapUtils.getObject(userExportRequest.getDistrict(), String.class))
                .setProvince(MapUtils.getObject(userExportRequest.getProvince(), String.class))
                .setYearsOfEx(MapUtils.getObject(userExportRequest.getYearsOfEx(), Integer.class))
                .setLocked(MapUtils.getObject(userExportRequest.getLocked(), Boolean.class))
                .setDeleted(MapUtils.getObject(userExportRequest.getDeleted(), Boolean.class))
                .setRoles(roles)
                .build();
        return userBuilder;
    }
}
