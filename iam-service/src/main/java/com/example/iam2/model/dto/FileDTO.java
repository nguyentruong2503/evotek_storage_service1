package com.example.iam2.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class FileDTO {
    private Long id;
    private String name;
    private BigInteger size;
    private String type;
    private String url;
    private Boolean is_public;
    private Long owner_id;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private byte[] content;
}
