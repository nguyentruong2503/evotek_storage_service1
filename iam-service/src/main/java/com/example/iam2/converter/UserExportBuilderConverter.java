package com.example.iam2.converter;

import com.example.iam2.builder.UserExportBuilder;
import com.example.iam2.model.request.UserExportRequest;
import com.example.iam2.util.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserExportBuilderConverter {
    public UserExportBuilder toUserExportBuilder (UserExportRequest userExportRequest){
        UserExportBuilder userExportBuilder = new UserExportBuilder.Builder()
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
                .build();
        return userExportBuilder;
    }
}
