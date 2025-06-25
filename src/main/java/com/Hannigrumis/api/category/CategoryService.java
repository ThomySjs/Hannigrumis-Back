package com.Hannigrumis.api.category;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.Hannigrumis.api.product.ProductRepository;
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

    public List<Category> getAll(String order) {
        List<String> sortTypes = List.of("id", "name", "description");
        if (order == null || !sortTypes.contains(order)) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findAll(Sort.by(order));
    }

    public Optional<Category> addCategory(String name, String description, MultipartFile file) {
        List<Category> exsists = categoryRepository.findByName(name);

        if (!exsists.isEmpty()) {
            return Optional.empty();
        }

        Category category = new Category(name, description);
        String imageName = imageService.createImageFile(file);
        category.setImagePath(imageName);

        return Optional.of(categoryRepository.saveAndFlush(category));
    }

    public Boolean deleteCategoryById(Long id) {
        try {
            Category category = categoryRepository.findById(id).get();
            productRepository.deleteProductByCategory(category);
            categoryRepository.deleteById(id);

            return true;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public Optional<Category> editCategory(Long id, String name, String description, MultipartFile file) {
        try{
            Category category = categoryRepository.findById(id).get();

            String imagePath = (file == null) ? category.getImagePath() : imageService.createImageFile(file);

            category.setName(name);
            category.setImagePath(imagePath);
            category.setDescription(description);

            categoryRepository.save(category);

            return Optional.of(category);
        }
        catch (NoSuchElementException e) {
            return Optional.empty();
        }

    }

    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

}
