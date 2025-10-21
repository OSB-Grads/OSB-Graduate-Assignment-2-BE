package com.bank.webApplication.Controllers;

import com.bank.webApplication.CustomException.ProductAlreadyExistException;
import com.bank.webApplication.CustomException.ProductNotFoundException;
import com.bank.webApplication.Dto.ProductDto;
import com.bank.webApplication.Services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ProductControllerTests {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDto sampleProduct;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    void testCreateProducts_Success() {
        //mock dto
        ProductDto product1 = new ProductDto();
        product1.setProductId("P001");
        product1.setProductName("Savings Account");
        product1.setInterestRate(3.5);
        product1.setFundingWindow(30);
        product1.setCoolingPeriod(5);
        product1.setTenure(365);
        product1.setDescription("Basic savings account with 3.5% interest");
        //when
        when(productService.createProduct(sampleProduct)).thenReturn(product1);
        //response
        ResponseEntity<ProductDto> response = productController.createProduct(sampleProduct);
        //assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleProduct.getProductId(), response.getBody().getProductId());
        assertEquals(sampleProduct.getProductName(), response.getBody().getProductName());
        assertEquals(sampleProduct.getInterestRate(), response.getBody().getInterestRate());
        assertEquals(sampleProduct.getFundingWindow(), response.getBody().getFundingWindow());
        assertEquals(sampleProduct.getCoolingPeriod(), response.getBody().getCoolingPeriod());
        assertEquals(sampleProduct.getDescription(), response.getBody().getDescription());
        assertEquals(sampleProduct.getTenure(), response.getBody().getTenure());
        //verify
        verify(productService, times(1)).createProduct(sampleProduct);
    }

    @Test
    void testUpdateProducts_Success() {
        //mock dto
        String id = "P001";
        ProductDto product1 = new ProductDto();
        product1.setProductId(id);
        product1.setProductName("Savings Account");
        product1.setInterestRate(4.5);
        product1.setFundingWindow(30);
        product1.setCoolingPeriod(5);
        product1.setTenure(365);
        product1.setDescription("Basic savings account with 3.5% interest");
        //when
        when(productService.updateProduct(id, sampleProduct)).thenReturn(product1);
        //response
        ResponseEntity<ProductDto> response = productController.updateProduct(id, sampleProduct);
        //assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product1.getProductId(), response.getBody().getProductId());
        assertEquals(product1.getProductName(), response.getBody().getProductName());
        assertEquals(product1.getInterestRate(), response.getBody().getInterestRate());
        assertEquals(product1.getFundingWindow(), response.getBody().getFundingWindow());
        assertEquals(product1.getCoolingPeriod(), response.getBody().getCoolingPeriod());
        assertEquals(product1.getDescription(), response.getBody().getDescription());
        assertEquals(product1.getTenure(), response.getBody().getTenure());


    }

    //test for delete_success
    @Test
    void testDeleteProducts_Success() {
        //dummy id
        String id = "P001";
        //Do Nothing
        doNothing().when(productService).deleteProduct(id);
        //response
        ResponseEntity<String> response = productController.deleteProduct(id);
        //assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product deleted Successfully", response.getBody());
        //verify
        verify(productService, times(1)).deleteProduct(id);
    }

    //CreateProduct Failure
    @Test
    void testCreateProduct_FailureProductAlreadyExist() {
        //when
        when(productService.createProduct(sampleProduct))
                .thenThrow(new ProductAlreadyExistException(" Product Already Exists"));
        //assert
        ProductAlreadyExistException ex = assertThrows(ProductAlreadyExistException.class,
                () -> productController.createProduct(sampleProduct));
        assertEquals(" Product Already Exists", ex.getMessage());
        //verify
        verify(productService, times(1)).createProduct(sampleProduct);

    }

    //UpdateProduct Failure
    @Test
    void testUpdateProduct_FailureProductDoesNotExist() {
        String id = "P001";
        when(productService.updateProduct(id, sampleProduct))
                .thenThrow(new ProductNotFoundException(" Product Not Found or does not exist in database"));
        //assert
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class,
                () -> productController.updateProduct(id, sampleProduct));
        assertEquals(" Product Not Found or does not exist in database", ex.getMessage());
        //verify
        verify(productService, times(1)).updateProduct(id, sampleProduct);
    }

    //DeleteProduct Failure
    @Test
    void testDeleteProduct_FailureProductDoesNotExist() {
        //dummy id
        String id = "P001";
        //do Throw when
        doThrow(new ProductNotFoundException(" Product Not Found or does not exist in the database")).when(productService).deleteProduct(id);
        //assert
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class,
                () -> productController.deleteProduct(id));
        assertEquals(" Product Not Found or does not exist in the database", ex.getMessage());
        //verify
        verify(productService, times(1)).deleteProduct(id);
    }

}

