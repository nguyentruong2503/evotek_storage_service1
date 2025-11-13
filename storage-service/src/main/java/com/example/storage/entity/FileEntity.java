package com.example.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata")
@Getter
@Setter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "size", nullable = false)
    private BigInteger size;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "is_public" ,nullable = false)
    private Boolean is_public;

    @Column(name = "owner_id", nullable = false)
    private Long owner_id;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted" ,nullable = false)
    private boolean deleted = false;
}
