package com.springboot.blog.repository.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositoryTruncation {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void truncateTable(String tableName) {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE "+ tableName).executeUpdate();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
    }

}
