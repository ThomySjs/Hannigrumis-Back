package com.Hannigrumis.api.product;

import com.Hannigrumis.api.category.Category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;


@Entity
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @ManyToOne()
    private Category category;
    private String imagePath;

    public Product() {
        
    }

    public Product(String name, Category category, String imagePath) {
        this.name = name;
        this.category = category;
        this.imagePath = imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategoryId() {
        return category;
    }

    public String getImagePath() {
        return imagePath;
    }
}
