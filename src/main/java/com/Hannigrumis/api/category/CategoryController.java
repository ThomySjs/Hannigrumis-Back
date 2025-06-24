package com.Hannigrumis.api.category;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping(path = "/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCategory(@RequestPart("name") String name, 
                                        @RequestPart("description") String description, 
                                        @RequestPart("file") MultipartFile file) {
        try {
            Category category = categoryService.addCategory(name, description, file).get();

            return ResponseEntity.ok(category);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("The category already exists.");
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(required=false) String order) {
        return ResponseEntity.ok(categoryService.getAll(order));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteCategoryByName(@PathVariable Long id) {

        if (categoryService.deleteCategoryById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Category not found.");
    }

    @PutMapping(path = "/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> editCategory(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam(required = false) MultipartFile file) {
        
        try {
            return ResponseEntity.ok(categoryService.editCategory(id, name, description, file).get());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Category not found.");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
}
