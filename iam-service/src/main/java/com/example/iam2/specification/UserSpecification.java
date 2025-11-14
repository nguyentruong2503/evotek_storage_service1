package com.example.iam2.specification;

import com.example.iam2.builder.UserBuilder;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.entity.UserEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<UserEntity> filter(UserBuilder builder) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addStringPredicate(predicates, cb, root, "username", builder.getUsername());
            addStringPredicate(predicates, cb, root, "email", builder.getEmail());
            addStringPredicate(predicates, cb, root, "firstName", builder.getFirstName());
            addStringPredicate(predicates, cb, root, "lastName", builder.getLastName());
            addStringPredicate(predicates, cb, root, "phone", builder.getPhone());
            addStringPredicate(predicates, cb, root, "street", builder.getStreet());
            addStringPredicate(predicates, cb, root, "ward", builder.getWard());
            addStringPredicate(predicates, cb, root, "district", builder.getDistrict());
            addStringPredicate(predicates, cb, root, "province", builder.getProvince());

            if (builder.getId() != null) {
                predicates.add(cb.equal(root.get("id"), builder.getId()));
            }
            if (builder.getYearsOfEx() != null) {
                predicates.add(cb.equal(root.get("yearsOfEx"), builder.getYearsOfEx()));
            }

            if (builder.getLocked() != null) {
                predicates.add(cb.equal(root.get("locked"), builder.getLocked()));
            }
            if (builder.getDeleted() != null) {
                predicates.add(cb.equal(root.get("deleted"), builder.getDeleted()));
            }

            if (builder.getBirthday() != null) {
                predicates.add(cb.equal(root.get("birthday"), builder.getBirthday()));
            }

            if (builder.getRoles() != null && !builder.getRoles().isEmpty()) {
                Join<UserEntity, RoleEntity> joinRoles = root.join("roles", JoinType.LEFT);
                predicates.add(joinRoles.get("code").in(builder.getRoles()));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addStringPredicate(List<Predicate> predicates, CriteriaBuilder cb,
                                           Root<UserEntity> root, String fieldName, String value) {
        if (value != null && !value.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%"));
        }
    }
}
