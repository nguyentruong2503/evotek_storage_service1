package com.example.storage.converter;

import com.example.common.model.dto.FileDTO;
import com.example.storage.entity.FileEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileConverter {

    @Autowired
    private ModelMapper modelMapper;

    public FileDTO toFileDTO (FileEntity fileEntity){
        return modelMapper.map(fileEntity, FileDTO.class);
    }
}
