package com.example.storage.builder;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class FileSearchBuilder {
    private Long id;
    private String name;
    private BigInteger size;
    private String type;
    private String url;
    private Boolean is_public;
    private Long owner_id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private FileSearchBuilder(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.size = builder.size;
        this.type = builder.type;
        this.url = builder.url;
        this.is_public = builder.is_public;
        this.owner_id = builder.owner_id;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigInteger getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public Boolean isIs_public() {
        return is_public;
    }

    public Long getOwner_id() {
        return owner_id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static class Builder{
        private Long id;
        private String name;
        private BigInteger size;
        private String type;
        private String url;
        private Boolean is_public;
        private Long owner_id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSize(BigInteger size) {
            this.size = size;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setURL(String url) {
            this.url = url;
            return this;
        }

        public Builder setIs_public(Boolean is_public){
            this.is_public = is_public;
            return this;
        }

        public Builder setOwner_id(Long owner_id) {
            this.owner_id = owner_id;
            return this;
        }

        public Builder setCreateAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdateAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public FileSearchBuilder build() {
            return new FileSearchBuilder(this);
        }
    }
}
