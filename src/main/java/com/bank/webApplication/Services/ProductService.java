package com.bank.webApplication.Services;


import com.bank.webApplication.CustomException.ProductNotFoundException;
import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DtoEntityMapper dtoEntityMapper;

    public ProductDto getProduct(String  productId){
        log.info("[ProductService] getProduct entered SUCCESS");
        ProductEntity product=productRepository.findById(productId)
                .orElseThrow(()->{
                    log.error("[ProductService] getProduct: not found FAILURE");
                    return new ProductNotFoundException(" Product Not Found, Invalid Id");});
        ProductDto productDto=dtoEntityMapper.convertToDto(product,ProductDto.class);
        log.info("[ProductService] getProduct  SUCCESS");
        return productDto;
    }
    public List<ProductDto> getAllProducts(){
        log.info("[ProductService] getAllProducts entered SUCCESS");
        List<ProductEntity> products=productRepository.findAll();

        List<ProductDto>productsdto=products.stream()
                .map(product->dtoEntityMapper.convertToDto(product,ProductDto.class))
                .collect(Collectors.toList());
        log.info("[ProductService] getAllProducts  SUCCESS");
        return productsdto;

    }
}
