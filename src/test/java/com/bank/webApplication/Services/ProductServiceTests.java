package com.bank.webApplication.Services;


import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private DtoEntityMapper dtoEntityMapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProduct(){

        String productId = "FD01";
        ProductEntity product = new ProductEntity();
        product.setProductId(productId);

        ProductDto productDto = new ProductDto();
        productDto.setProductId(productId);

        //Mock Behaviour
        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(product));
        when(dtoEntityMapper.convertToDto(product,ProductDto.class)).thenReturn(productDto);

        // Logic
        ProductDto result = productService.getProduct(productId);

        //Assertions
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        verify(productRepository).findById(productId);
        verify(dtoEntityMapper).convertToDto(product,ProductDto.class);
    }

    @Test
    void testGetProduct_NotFound(){

        String productId = "1234";

        //Mock Behaviour
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Logic
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            productService.getProduct(productId);
        });

        //Assertions
        verify(productRepository).findById(productId);
        verifyNoInteractions(dtoEntityMapper);
    }

    @Test
    void testGetAllProducts(){
        ProductEntity product1 = new ProductEntity();
        product1.setProductId("FD01");
        ProductEntity product2 = new ProductEntity();
        product2.setProductId("FD01");

        ProductDto productDto1 = new ProductDto();
        productDto1.setProductId("SV02");
        ProductDto productDto2 = new ProductDto();
        productDto2.setProductId("SV02");

        //Mock Behaviour
        List<ProductEntity> products = List.of(product1,product2);
        when(productRepository.findAll()).thenReturn(products);
        when(dtoEntityMapper.convertToDto(product1, ProductDto.class)).thenReturn(productDto1);
        when(dtoEntityMapper.convertToDto(product2, ProductDto.class)).thenReturn(productDto2);

        //Logic
        List<ProductDto> result = productService.getAllProducts();

        //Assertions
        assertNotNull(result);
        assertEquals(productDto1.getProductId(), result.getFirst().getProductId());
        assertEquals(productDto2.getProductId(), result.get(1).getProductId());
        assertEquals(2, result.size());
        verify(productRepository).findAll();
        verify(productRepository).findAll();
        verify(dtoEntityMapper).convertToDto(product1,ProductDto.class);
        verify(dtoEntityMapper,times(2)).convertToDto(any(ProductEntity.class),eq(ProductDto.class));
    }

    @Test
    void testGetAllProducts_ThrowsException() {
        // Mock Behaviour
        when(productRepository.findAll()).thenThrow(new RuntimeException("Error: No Products Found in Database. "));

        //Logic
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getAllProducts();
        });

        //Assertions
        verify(productRepository).findAll();
        verifyNoInteractions(dtoEntityMapper);
    }

}
