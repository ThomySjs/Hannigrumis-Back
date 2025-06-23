package com.Hannigrumis.api.product;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.Hannigrumis.api.productImages.ImageService;

import java.util.List;
import java.util.Optional;

import com.Hannigrumis.api.category.Category;
import com.Hannigrumis.api.category.CategoryService;

@Component
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ImageService imageService;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, ImageService imageService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.imageService = imageService;
        this.categoryService = categoryService;
    }

    public List<Product> getProducts(String order) {
        List<String> sortTypes = List.of("category", "name", "id");
        if (order == null || !sortTypes.contains(order)) {
            return productRepository.findAll();
        }
        else if (order.equals("category")) {
            return productRepository.getAllByCategory();
        }
        return productRepository.findAll(Sort.by(order));
    }

    public Product addProduct(String name, Long categoryId, MultipartFile file) {
        
        Optional<Category> categoryCheck = categoryService.findCategoryById(categoryId);

        if (!categoryCheck.isPresent()) {
            return null;
        }

        Category category = categoryCheck.get();

        Product product = new Product(name, category, null);

        String imagePath = imageService.createImageFile(file);
        product.setImagePath(imagePath);

        return productRepository.save(product);
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> editProduct(Long id, String name, Long categoryId, MultipartFile file) {
        Optional<Product> productCkeck = productRepository.findById(id);
        Optional<Category> categoryCheck = categoryService.findCategoryById(categoryId);

        
        if (!productCkeck.isPresent() || !categoryCheck.isPresent()) {
            return Optional.empty();
        }
        Product product = productCkeck.get();
        String imagePath = (file == null) ? productCkeck.get().getImagePath() : imageService.createImageFile(file);
        
        product.setName(name);
        product.setCategory(categoryCheck.get());
        product.setImagePath(imagePath);

        productRepository.save(product);

        return Optional.of(product);
    }

}
