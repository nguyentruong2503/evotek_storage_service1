package com.example.storage.converter;

import com.example.common.model.request.FileSearchRequest;
import com.example.storage.builder.FileSearchBuilder;
import com.example.common.util.MapUtils;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
public class FileSearchBuilderConverter {

    public FileSearchBuilder toFileSearchBuilder (FileSearchRequest fileSearchRequest){
        FileSearchBuilder fileSearchBuilder = new FileSearchBuilder.Builder()
                .setName(MapUtils.getObject(fileSearchRequest.getName(),String.class))
                .setSize(MapUtils.getObject(fileSearchRequest.getSize(), BigInteger.class))
                .setType(MapUtils.getObject(fileSearchRequest.getType(), String.class))
                .setURL(MapUtils.getObject(fileSearchRequest.getUrl(), String.class))
                .setOwner_id(MapUtils.getObject(fileSearchRequest.getOwner_id(), Long.class))
                .setIs_public(MapUtils.getObject(fileSearchRequest.getIs_public(), Boolean.class))
                .setCreateAt(MapUtils.getObject(fileSearchRequest.getCreatedAt(), LocalDateTime.class))
                .setUpdateAt(MapUtils.getObject(fileSearchRequest.getUpdatedAt(), LocalDateTime.class))
                .build();
        return fileSearchBuilder;
    }
}
