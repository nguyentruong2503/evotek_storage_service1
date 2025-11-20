package com.example.storage.repository;

import com.example.storage.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor {
    boolean existsByHash (String hash);
}
