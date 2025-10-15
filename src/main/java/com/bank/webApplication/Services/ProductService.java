package com.bank.webApplication.Services;


import com.bank.webApplication.CustomException.ProductAlreadyExistException;
import com.bank.webApplication.CustomException.ProductNotFoundException;
import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DtoEntityMapper dtoEntityMapper;

    //method to get Product
    public ProductDto getProduct(String productId) {
        log.info("[ProductService] getProduct entered SUCCESS");
        //check if product is present in the database
        ProductEntity product = productRepository.findByProductId(productId);
        if (product == null) {
            log.error("[ProductService] getProduct: not found FAILURE");
            throw new ProductNotFoundException(" Product Not Found, Invalid Id");
        }
        //convert to dto from entity
        ProductDto productDto = dtoEntityMapper.convertToDto(product, ProductDto.class);
        log.info("[ProductService] getProduct  SUCCESS");
        //return
        return productDto;
    }

    //method to get all Products
    public List<ProductDto> getAllProducts() {
        log.info("[ProductService] getAllProducts entered SUCCESS");
        //get all products from the database
        List<ProductEntity> products = productRepository.findAll();
        //convert all the product from entity to dto
        List<ProductDto> productsdto = products.stream()
                .map(product -> dtoEntityMapper.convertToDto(product, ProductDto.class))
                .collect(Collectors.toList());
        log.info("[ProductService] getAllProducts  SUCCESS");
        //return
        return productsdto;

    }

    //method to create a new Product
    public ProductDto createProduct(ProductDto productDto) {
        log.info("[ProductService] createproduct entered SUCCESS");
        //check if the product is already present in the database
        ProductEntity product = productRepository.findByProductId(productDto.getProductId());
        if (product != null) {
            log.error("[ProductService] createProduct: Product Already Exists FAILURE");
            throw new ProductAlreadyExistException(" Product Already Exists");
        }
        //convert from dto to entity
        ProductEntity productEntity = dtoEntityMapper.convertToEntity(productDto, ProductEntity.class);
        log.info("[ProductService] createProduct: New Product created and saved into database");
        //save in the database
        ProductEntity save = productRepository.save(productEntity);
        //convert to dto
        ProductDto response = dtoEntityMapper.convertToDto(save, ProductDto.class);
        log.info("[ProductService] createProduct  SUCCESS");
        //return
        return response;
    }

    @Transactional
    //method to updateproduct
    public ProductDto updateProduct(String productId, ProductDto productDto) {
        log.info("[ProductService] updateProduct entered SUCCESS");
        //check if product is present in the database
        ProductEntity product = productRepository.findByProductId(productId);
        if (product == null) {
            log.error("[ProductService] updateProduct: not found FAILURE");
            throw new ProductNotFoundException(" Product Not Found or does not exist in database");
        }
        //populate the updated values into the entity
        product.setProductName(productDto.getProductName());
        product.setInterestRate(productDto.getInterestRate());
        product.setFundingWindow(productDto.getFundingWindow());
        product.setCoolingPeriod(productDto.getCoolingPeriod());
        product.setTenure(productDto.getTenure());
        product.setDescription(productDto.getDescription());
        log.info("[ProductService] updateProduct:  Product updated and saved into database");
        //save in the database
        ProductEntity update = productRepository.save(product);
        log.info("[ProductService] updateProduct  SUCCESS");
        //convert to dto and return
        return (dtoEntityMapper.convertToDto(update, ProductDto.class));
    }

    //method to delete Product
    public void deleteProduct(String productId) {
        log.info("[ProductService] deleteProduct entered SUCCESS");
        //check if the product is present in the database for deletion
        ProductEntity product = productRepository.findByProductId(productId);
        if (product == null) {
            log.error("[ProductService] Product: not found FAILURE");
            throw new ProductNotFoundException(" Product Not Found or does not exist in the database");
        }
        log.info("[ProductService] Product Deleted From The Database  SUCCESS");
        //remove product from the database
        productRepository.deleteById(productId);
        log.info("[ProductService] deleteProduct  SUCCESS");
    }


}
