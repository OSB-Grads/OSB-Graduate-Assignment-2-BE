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
    @Mock
    private ProductDto productDto1;
    @Mock
    private ProductEntity product1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDto1 = new ProductDto();
        productDto1.setProductId("FD0043");
        productDto1.setProductName("3  pLAN");
        productDto1.setInterestRate(1.0);
        productDto1.setFundingWindow(3);
        productDto1.setDescription("3 year plan with the interestRate of 6.1");
        productDto1.setTenure(10);

        product1 = new ProductEntity();
        product1.setProductId("FD0043");
        product1.setProductName("3  pLAN");
        product1.setInterestRate(1.0);
        product1.setFundingWindow(3);
        product1.setDescription("3 year plan with the interestRate of 6.1");
        product1.setTenure(10);
        productRepository.save(product1);
    }


    @Test
    void testGetProduct() {

        String productId = "FD01";
        ProductEntity product = new ProductEntity();
        product.setProductId(productId);

        ProductDto productDto = new ProductDto();
        productDto.setProductId(productId);

        //Mock Behaviour
        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(product));
        when(dtoEntityMapper.convertToDto(product, ProductDto.class)).thenReturn(productDto);

        // Logic
        ProductDto result = productService.getProduct(productId);

        //Assertions
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        verify(productRepository).findById(productId);
        verify(dtoEntityMapper).convertToDto(product, ProductDto.class);
    }

    @Test
    void testGetProduct_NotFound() {

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
    void testGetAllProducts() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId("FD01");
        ProductEntity product2 = new ProductEntity();
        product2.setProductId("FD01");

        ProductDto productDto1 = new ProductDto();
        productDto1.setProductId("SV02");
        ProductDto productDto2 = new ProductDto();
        productDto2.setProductId("SV02");

        //Mock Behaviour
        List<ProductEntity> products = List.of(product1, product2);
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
        verify(dtoEntityMapper).convertToDto(product1, ProductDto.class);
        verify(dtoEntityMapper, times(2)).convertToDto(any(ProductEntity.class), eq(ProductDto.class));
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

    @Test
    void testcreateProducts_Success() {

        when(dtoEntityMapper.convertToEntity(productDto1, ProductEntity.class)).thenReturn(product1);
        when(productRepository.save(product1)).thenReturn(product1);
        when(dtoEntityMapper.convertToDto(product1, ProductDto.class)).thenReturn(productDto1);

        ProductDto response = productService.createProduct(productDto1);

        assertNotNull(response);

    }

    @Test
    void testcreateProducts_FailureUserAlreadyExist() {

        when(productRepository.findById("FD0043")).thenReturn(Optional.of(product1));
        //mock
        Exception e = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto1);
        });
        //assert
        assertEquals(" Product Already Exists", e.getMessage());
    }

    @Test
    void testupdateProducts_Success() {

        when(productRepository.findById("FD0043")).thenReturn(Optional.of(product1));
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

    @Test
    void testupdateProducts_FailureUserDoesNotExist() {

        //mock dto
        when(productRepository.findById("FD0043")).thenReturn(Optional.empty());
        Exception e = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct("FD0043", productDto1);
        });
        //assert
        assertEquals(" Product Not Found or does not exist in database", e.getMessage());
    }

    @Test
    void testdeleteProducts_Success() {

        when(productRepository.findById("FD0043")).thenReturn(Optional.of(product1));
        productService.deleteProduct("FD0043");
    }

    @Test
    void testdeleteProducts_FailureUserDoesNotExist() {

        when(productRepository.findById("FD0043")).thenReturn(Optional.empty());
        Exception e = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct("FD0043");
            ;
        });
        //assert
        assertEquals(" Product Not Found or does not exist in the database", e.getMessage());
    }

}
