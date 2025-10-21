package com.bank.webApplication.Services;
import com.bank.webApplication.CustomException.ProductAlreadyExistException;
import com.bank.webApplication.CustomException.ProductNotFoundException;
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

    public ProductDto productDto1, productDto2;
    public ProductEntity product1, product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDto1 = new ProductDto("FD0043","3 plan",1.0,3,0,10,"3 year plan with the interestRate of 6.1");
        product1 = new ProductEntity("FD0043","3 plan",1.0,3,0,10,"3 year plan with the interestRate of 6.1");
//        productRepository.save(product1);
    }

    //Test - getProduct - when product exists
    @Test
    void testGetProduct_Success() {

        //Mock Behaviour
        when(productRepository.findByProductId(product1.getProductId())).thenReturn(product1);
        when(dtoEntityMapper.convertToDto(product1, ProductDto.class)).thenReturn(productDto1);

        // Logic
        ProductDto result = productService.getProduct(product1.getProductId());

        //Assertions
        assertNotNull(result);
        assertEquals(productDto1.getProductId(), result.getProductId());
        verify(productRepository,times(1)).findByProductId(product1.getProductId());
        verify(dtoEntityMapper,times(1)).convertToDto(product1, ProductDto.class);
    }

    //Test - getProduct - when product doesnt exists
    @Test
    void testGetProduct_NotFound() {

        String productId = "1234";
        //Mock Behaviour
        when(productRepository.findByProductId(productId)).thenReturn(null);
        // Logic
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(productId);
        });

        //Assertions
        assertEquals(" Product Not Found, Invalid Id", exception.getMessage());
        verify(productRepository,times(1)).findByProductId(productId);
        verifyNoInteractions(dtoEntityMapper);
    }
    //Test - getAllProducts - When records exist
    @Test
    void testGetAllProducts() {

        product2 = new ProductEntity("FD0044","1 plan",1.0,3,0,10,"1 year plan with the interestRate of 6.1");
        productDto2 = new ProductDto("FD0044","1 plan",1.0,3,0,10,"1 year plan with the interestRate of 6.1");

        //Mock Behaviour
        List<ProductEntity> products = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(products);
        when(dtoEntityMapper.convertToDto(product1, ProductDto.class)).thenReturn(productDto1);
        when(dtoEntityMapper.convertToDto(product2, ProductDto.class)).thenReturn(productDto2);

        //Logic
        List<ProductDto> result = productService.getAllProducts();

        //Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(productDto1.getProductId(), result.get(0).getProductId());
        assertEquals(productDto2.getProductId(), result.get(1).getProductId());
        verify(productRepository).findAll();
        verify(dtoEntityMapper).convertToDto(product1, ProductDto.class);
        verify(dtoEntityMapper).convertToDto(product2, ProductDto.class);
        verify(dtoEntityMapper, times(2)).convertToDto(any(ProductEntity.class), eq(ProductDto.class));
    }

    //Tets - getAllProducts - When No records exists
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

    //Test - createProduct
    @Test
    void testcreateProducts_Success() {

        when(dtoEntityMapper.convertToEntity(productDto1, ProductEntity.class)).thenReturn(product1);
        when(productRepository.save(product1)).thenReturn(product1);
        when(dtoEntityMapper.convertToDto(product1, ProductDto.class)).thenReturn(productDto1);

        ProductDto response = productService.createProduct(productDto1);

        assertNotNull(response);

    }

    //Test - createProduct - product already exists
    @Test
    void testcreateProducts_Failure() {

        when(productRepository.findByProductId("FD0043")).thenReturn(product1);
        //mock
        Exception ex = assertThrows(ProductAlreadyExistException.class, () -> {
            productService.createProduct(productDto1);
        });

        //assert
        assertEquals("Product Already Exists", ex.getMessage());
    }

    //Test - update Products - Success
    @Test
    void testupdateProducts_Success() {

        when(productRepository.findByProductId("FD0043")).thenReturn(product1);
        product1.setProductName(productDto1.getProductName());
        product1.setInterestRate(productDto1.getInterestRate());
        product1.setFundingWindow(productDto1.getFundingWindow());
        product1.setCoolingPeriod(productDto1.getCoolingPeriod());
        product1.setTenure(productDto1.getTenure());
        product1.setDescription(productDto1.getDescription());
        when(productRepository.save(product1)).thenReturn(product1);
        when(dtoEntityMapper.convertToDto(product1, ProductDto.class)).thenReturn(productDto1);
        ProductDto response = productService.updateProduct("FD0043", productDto1);
        assertNotNull(response);
    }

    //Test - updateProduct - User DoesNot Exist
    @Test
    void testupdateProducts_Failure() {

        when(productRepository.findByProductId("FD0043")).thenReturn(null);
        Exception e = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct("FD0043", productDto1);
        });
        //assert
        assertEquals(" Product Not Found or does not exist in database", e.getMessage());
    }

    // Test - deleteProduct - Success
    @Test
    void testdeleteProducts_Success() {

        when(productRepository.findByProductId("FD0043")).thenReturn(product1);

        productService.deleteProduct("FD0043");

        verify(productRepository).deleteById("FD0043");
    }

    // Test - deleteProduct - User DoesNot Exist
    @Test
    void testdeleteProducts_Failure() {

        when(productRepository.findByProductId("FD0043")).thenReturn(null);
        Exception e = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct("FD0043");
            ;
        });
        //assert
        assertEquals(" Product Not Found or does not exist in the database", e.getMessage());
    }

}
