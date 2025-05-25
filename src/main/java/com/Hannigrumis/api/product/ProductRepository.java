package com.Hannigrumis.api.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Hannigrumis.api.category.Category;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Product product SET product.name = :name, product.category = :category, product.imagePath = :imagePath WHERE product.id = :id")
    int updateProduct(@Param("id") Long id, @Param("name") String name, @Param("category") Category category, @Param("imagePath") String imagePath);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.category = :category")
    int deleteProductByCategory(@Param("category") Category category);
}
