package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.http.ResponseEntity;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ProductControllerTests {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDto sampleProduct;

    @BeforeEach
    void setup() {
        sampleProduct = new ProductDto();
        sampleProduct.setProductId("P001");
        sampleProduct.setProductName("Savings Account");
        sampleProduct.setInterestRate(3.5);
        sampleProduct.setFundingWindow(30);
        sampleProduct.setCoolingPeriod(5);
        sampleProduct.setTenure(365);
        sampleProduct.setDescription("Basic savings account with 3.5% interest");
    }

    @Test
    void testFetchProductById_Success() {
        // Given
        when(productService.getProduct("P001")).thenReturn(sampleProduct);

        // When
        ResponseEntity<?> response = productController.fetchProductDetailsById("P001");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ProductDto);

        ProductDto returned = (ProductDto) response.getBody();
        assertEquals("P001", returned.getProductId());
        assertEquals("Savings Account", returned.getProductName());
        assertEquals(3.5, returned.getInterestRate());
        assertEquals(30, returned.getFundingWindow());
        assertEquals(5, returned.getCoolingPeriod());
        assertEquals(365, returned.getTenure());
        assertEquals("Basic savings account with 3.5% interest", returned.getDescription());

        verify(productService, times(1)).getProduct("P001");
    }

    @Test
    void testFetchProductById_NotFound() {
        // Given
        when(productService.getProduct("INVALID")).thenReturn(null);

        // When
        ResponseEntity<?> response = productController.fetchProductDetailsById("INVALID");

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue()); // controller currently returns 200
        assertNull(response.getBody());

        verify(productService, times(1)).getProduct("INVALID");
    }

    @Test
    void testFetchProductById_ServiceThrowsException() {
        when(productService.getProduct("P001"))
                .thenThrow(new RuntimeException("Database down"));

        assertThrows(RuntimeException.class, () -> {
            productController.fetchProductDetailsById("P001");
        });

        verify(productService, times(1)).getProduct("P001");
    }

    @Test
    void testFetchAllProducts_Success() {
        // Given
        ProductDto product1 = new ProductDto();
        product1.setProductId("P001");
        product1.setProductName("Savings Account");
        product1.setInterestRate(3.5);
        product1.setFundingWindow(30);
        product1.setCoolingPeriod(5);
        product1.setTenure(365);
        product1.setDescription("Basic savings account");

        ProductDto product2 = new ProductDto();
        product2.setProductId("P002");
        product2.setProductName("Current Account");
        product2.setInterestRate(0.0);
        product2.setFundingWindow(0);
        product2.setCoolingPeriod(0);
        product2.setTenure(0);
        product2.setDescription("Business current account");

        List<ProductDto> products = List.of(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When
        ResponseEntity<?> response = productController.fetchProductDetails();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);

        List<ProductDto> returned = (List<ProductDto>) response.getBody();
        assertEquals(2, returned.size());
        assertEquals("P001", returned.get(0).getProductId());
        assertEquals("P002", returned.get(1).getProductId());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testFetchAllProducts_EmptyList() {
        // Given
        when(productService.getAllProducts()).thenReturn(List.of());

        // When
        ResponseEntity<?> response = productController.fetchProductDetails();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);

        List<ProductDto> returned = (List<ProductDto>) response.getBody();
        assertTrue(returned.isEmpty());

        verify(productService, times(1)).getAllProducts();
        }
    }

