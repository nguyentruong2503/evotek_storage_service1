package com.example.iam2.converter;

import com.example.iam2.entity.UserEntity;
import com.example.common.model.dto.RoleDTO;
import com.example.common.model.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserConverter {

    @Autowired
    private ModelMapper modelMapper;

    public UserDTO toUserDTO(UserEntity userEntity){
        UserDTO userDTO = modelMapper.map(userEntity, UserDTO.class);
        Set<RoleDTO> roleDTOs = userEntity.getRoles().stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .collect(Collectors.toSet());
        userDTO.setRoleDTOList(roleDTOs);
        return userDTO;
    }

}
