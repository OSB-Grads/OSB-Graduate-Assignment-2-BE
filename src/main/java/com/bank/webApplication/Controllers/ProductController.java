package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/fetch/{productId}")
    public ResponseEntity<?> fetchProductDetailsById(@PathVariable("productId") String productId) {
        log.info("[ProductController] pinged fetchProductDetailsById");
        ProductDto productInfo = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productInfo);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchProductDetails() {
        log.info("[ProductController] pinged fetchProductDetails");
        List<ProductDto> productInfo = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        log.info("[ProductController] pinged createProduct");
        ProductDto create = productService.createProduct(productDto);
        return ResponseEntity.ok(create);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String productId, @RequestBody ProductDto productDto) {
        log.info("[ProductController] pinged updateProduct");
        ProductDto update = productService.updateProduct(productId, productDto);
        return ResponseEntity.ok(update);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        log.info("[ProductController] pinged deleteProduct");
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted Successfully");
    }


}
