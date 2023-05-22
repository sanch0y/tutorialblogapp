package com.springboot.blog.repository;

import com.springboot.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Optional<Category> findFirstByOrderById();
}
