package com.Hannigrumis.api.product;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Hannigrumis.api.category.CategoryService;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping(path = "/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
    }

    @CrossOrigin
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required=false) String order) {
        return ResponseEntity.ok(productService.getProducts(order));
    }

    @CrossOrigin
    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addProduct(@RequestParam String name,
                                        @RequestParam Long categoryId,
                                        @RequestParam MultipartFile file                        
    ) {
            Product product = productService.addProduct(name, categoryId, file); 

            if (product == null) {
                return ResponseEntity.badRequest().body("Invalid category.");
            }

            return ResponseEntity.ok(product);
    }

    @CrossOrigin
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/edit", consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> editProduct(@RequestParam Long id,
                                        @RequestParam String name,
                                        @RequestParam Long categoryId,
                                        @RequestParam(required = false) MultipartFile file
    ){
        Optional<Product> edited = productService.editProduct(id, name, categoryId, file);
        if (edited.isPresent()) {
            return ResponseEntity.ok(edited.get());
        }
        return ResponseEntity.badRequest().build();
    }
    
    
}
