package com.example.storage.repository;

import com.example.storage.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long>, FileRepositoryCustom {
}
