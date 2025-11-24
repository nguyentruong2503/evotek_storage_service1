package com.example.storage.specification;

import com.example.storage.builder.FileSearchBuilder;
import com.example.storage.entity.FileEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FileSpecification {
    public static Specification<FileEntity> filter(FileSearchBuilder builder) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addStringPredicate(predicates, cb, root, "name", builder.getName());
            addStringPredicate(predicates, cb, root, "type", builder.getType());
            addStringPredicate(predicates, cb, root, "url", builder.getUrl());


            if (builder.getId() != null) {
                predicates.add(cb.equal(root.get("id"), builder.getId()));
            }

            if (builder.getOwner_id() != null) {
                predicates.add(cb.equal(root.get("owner_id"), builder.getOwner_id()));
            }

            if (builder.getSize() != null) {
                predicates.add(cb.equal(root.get("size"), builder.getSize()));
            }

            if (builder.isIs_public() != null) {
                predicates.add(cb.equal(root.get("is_public"), builder.isIs_public()));
            }

            if (builder.getCreatedAt() != null) {
                predicates.add(cb.equal(root.get("created_at"), builder.getCreatedAt()));
            }

            if (builder.getUpdatedAt() != null) {
                predicates.add(cb.equal(root.get("updated_at"), builder.getUpdatedAt()));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addStringPredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                           Root<FileEntity> root, String fieldName, String value) {
        if (value != null && !value.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%"));
        }
    }
}
