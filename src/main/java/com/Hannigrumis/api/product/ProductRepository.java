package com.Hannigrumis.api.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import com.Hannigrumis.api.category.Category;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.category = :category")
    int deleteProductByCategory(@Param("category") Category category);

    List<Product> findAll(Sort sort);

    @Query("SELECT p FROM Product p JOIN p.category c ORDER BY c.name")
    List<Product> getAllByCategory();
}
