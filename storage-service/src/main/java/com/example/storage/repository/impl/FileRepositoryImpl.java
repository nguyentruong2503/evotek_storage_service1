package com.example.storage.repository.impl;

import com.example.storage.builder.FileSearchBuilder;
import com.example.storage.entity.FileEntity;
import com.example.storage.repository.FileRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FileRepositoryImpl implements  FileRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public static void queryNormal(FileSearchBuilder fileSearchBuilder, StringBuilder where) {
        try {
            Field[] fields = FileSearchBuilder.class.getDeclaredFields();
            for (Field item : fields) {
                item.setAccessible(true);
                String fieldName = item.getName();
                Object value = item.get(fileSearchBuilder);

                if (value == null) continue;

                Class<?> type = item.getType();

                // String
                if (type == String.class) {
                    String str = value.toString().trim();
                    if (!str.isEmpty()) {
                        where.append(" AND f.").append(fieldName).append(" ILIKE '%").append(str).append("%' ");
                    }
                }
                // Number
                else if (type == Long.class || type == Integer.class || type == BigInteger.class) {
                    where.append(" AND f.").append(fieldName).append(" = ").append(value);
                }
                // Boolean
                else if (type == boolean.class || type == Boolean.class) {
                    where.append(" AND f.").append(fieldName).append(" = ").append((Boolean) value ? "TRUE" : "FALSE");
                }
                // LocalDateTime
                else if (type == LocalDateTime.class) {
                    LocalDateTime dateTime = (LocalDateTime) value;
                    String formatted = dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    where.append(" AND f.").append(fieldName).append(" >= '").append(formatted).append("' ");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<FileEntity> getAll(FileSearchBuilder builder, int page, int size) {
        StringBuilder sql = new StringBuilder("SELECT distinct f.* FROM file_metadata f ");
        StringBuilder where = new StringBuilder(" WHERE 1=1 AND deleted = false ");
        queryNormal(builder, where);
        sql.append(where);

        Query query = entityManager.createNativeQuery(sql.toString(), FileEntity.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countAll(FileSearchBuilder builder) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT f.id) FROM file_metadata f ");
        StringBuilder where = new StringBuilder(" WHERE 1=1 AND deleted = false ");
        queryNormal(builder, where);
        sql.append(where);

        Query query = entityManager.createNativeQuery(sql.toString());
        Number result = (Number) query.getSingleResult();
        return result.intValue();
    }
}
