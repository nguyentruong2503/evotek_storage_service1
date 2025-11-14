package com.example.iam2.repository;

import com.example.iam2.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phoneNumber);

    Optional<UserEntity> findByUsernameAndLockedAndDeleted(String username, boolean locked, boolean deleted);

    @Query(value = "SELECT DISTINCT u FROM UserEntity u\n" +
            "    LEFT JOIN FETCH u.roles r\n" +
            "    LEFT JOIN FETCH r.permissions p\n" +
            "    WHERE u.username = :username\n" +
            "      AND u.locked = false\n" +
            "      AND u.deleted = false\n" +
            "      AND r.deleted = false\n" +
            "      AND p.deleted = false")
    Optional<UserEntity> findByUsernameWithRolesAndPermissions(@Param("username") String username);
}
