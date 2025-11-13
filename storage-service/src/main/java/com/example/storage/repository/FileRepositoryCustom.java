package com.example.storage.repository;

import com.example.storage.builder.FileSearchBuilder;
import com.example.storage.entity.FileEntity;

import java.util.List;

public interface FileRepositoryCustom {
    List<FileEntity> getAll(FileSearchBuilder builder, int page, int size);
    long countAll(FileSearchBuilder builder);
}
