package com.bank.webApplication.Controllers;


import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @GetMapping("/fetch/{productId}")
    public ResponseEntity<?> fetchProductDetailsById(@PathVariable("productId") String productId){
        log.info("[ProductController] pinged fetchProductDetailsById");
        ProductDto productInfo=productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productInfo);
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchProductDetails(){
        log.info("[ProductController] pinged fetchProductDetails");
        List<ProductDto> productInfo=productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productInfo);
    }

}
