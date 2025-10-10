package com.bank.webApplication.Services;


import com.bank.webApplication.CustomException.ProductNotFoundException;
import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DtoEntityMapper dtoEntityMapper;

    public ProductDto getProduct(String  productId){
        ProductEntity product=productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException(" Product Not Found, Invalid Id"));
        ProductDto productDto=dtoEntityMapper.convertToDto(product,ProductDto.class);
        return productDto;
    }
    public List<ProductDto> getAllProducts(){
        List<ProductEntity> products=productRepository.findAll();

        List<ProductDto>productsdto=products.stream()
                .map(product->dtoEntityMapper.convertToDto(product,ProductDto.class))
                .collect(Collectors.toList());

        return productsdto;

    }
}
