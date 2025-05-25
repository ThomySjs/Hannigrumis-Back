package com.Hannigrumis.api.category;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.Hannigrumis.api.product.ProductRepository;
import com.Hannigrumis.api.product.ProductService;
import com.Hannigrumis.api.productImages.ImageService;

@Component
public class CategoryService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public CategoryService(CategoryRepository categoryRepository, ImageService imageService, ProductRepository productRepository){
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
        this.productRepository = productRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> addCategory(String name, String description, MultipartFile file) {
        Optional<Category> savedCategory = categoryRepository.findById(name);

        if (savedCategory.isPresent()) {
            return Optional.empty();
        }

        Category category = new Category(name, description);
        String imageName = imageService.createImageFile(file);
        category.setImagePath(imageName);

        return Optional.of(categoryRepository.saveAndFlush(category));
    }

    public Boolean deleteCategoryByName(String name) {
        try {
            Category category = categoryRepository.findById(name).get();
            productRepository.deleteProductByCategory(category);
            categoryRepository.deleteById(name);

            return true;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findById(name);
    }

}
