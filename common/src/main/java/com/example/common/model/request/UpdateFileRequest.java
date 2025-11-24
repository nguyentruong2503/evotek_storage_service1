package com.example.common.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFileRequest {
    private String name;
    private Boolean isPublic;
    private Long ownerId;
}

