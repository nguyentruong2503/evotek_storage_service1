package com.example.iam2.repository.impl;

import com.example.iam2.builder.UserExportBuilder;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserEntity> getAll(int page, int size) {
        String sql = "SELECT * FROM users"; // lấy tất cả, không lọc locked/delete
        Query query = entityManager.createNativeQuery(sql, UserEntity.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public Optional<UserEntity> findByUsernameWithRolesAndPermissions(String username) {
        String jpql = "SELECT u FROM UserEntity u " +
                "LEFT JOIN FETCH u.roles r " +
                "LEFT JOIN FETCH r.permissions p " +
                "WHERE u.username = '" + username + "' " +
                "AND u.locked = false " +
                "AND u.deleted = false " +
                "AND (r.deleted = false) " +
                "AND (p.deleted = false)";

        List<UserEntity> results = entityManager.createQuery(jpql, UserEntity.class)
                .getResultList();

        return results.stream().findFirst();
    }

    public static void queryNormal(UserExportBuilder filter, StringBuilder where) {
        try {
            Field[] fields = UserExportBuilder.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(filter);
                if (value == null) continue;

                String fieldName = field.getName();
                Class<?> type = field.getType();

                if (type == String.class) {
                    String str = value.toString().trim();
                    if (!str.isEmpty()) {
                        String safeStr = str.replace("'", "''");
                        where.append(" AND u.").append(fieldName)
                                .append(" ILIKE '%").append(safeStr).append("%' ");
                    }
                } else if (Number.class.isAssignableFrom(type)) {
                    where.append(" AND u.").append(fieldName).append(" = ").append(value);
                } else if (type == Boolean.class || type == boolean.class) {
                    where.append(" AND u.").append(fieldName)
                            .append(" = ").append((Boolean) value ? "TRUE" : "FALSE");
                } else if (type == Date.class) {
                    Date date = (Date) value;
                    String formatted = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    where.append(" AND u.").append(fieldName).append(" = '").append(formatted).append("' ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserEntity> importByFilter(UserExportBuilder builder) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT u.* FROM users u ");
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        queryNormal(builder, where);
        sql.append(where);

        Query query = entityManager.createNativeQuery(sql.toString(), UserEntity.class);
        return query.getResultList();
    }


}
