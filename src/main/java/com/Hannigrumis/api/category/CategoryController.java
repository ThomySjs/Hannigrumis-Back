package com.Hannigrumis.api.category;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping(path = "/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @CrossOrigin
    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCategory(@RequestPart("name") String name, @RequestPart("description") String description, @RequestPart("file") MultipartFile file) {
        try {
            Category category = categoryService.addCategory(name, description, file).get();

            return ResponseEntity.ok(category);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("The category already exists.");
        }
    }
    
    @CrossOrigin
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @CrossOrigin
    @DeleteMapping(path = "/delete/{name}")
    public ResponseEntity<?> deleteCategoryByName(@PathVariable String name) {

        if (categoryService.deleteCategoryByName(name)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Category not found.");
    }
    
    
}
