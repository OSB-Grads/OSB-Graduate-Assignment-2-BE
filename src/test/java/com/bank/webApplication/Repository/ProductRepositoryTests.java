package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTests {
    @Mock
    private ProductRepository productRepository;

    @Test
    void testSaveAndFindByProductId() {
        String productId = "FD01";
        ProductEntity product = new ProductEntity();
        product.setProductId(productId);
        product.setInterestRate(6.1);
        product.setFundingWindow(3);
        product.setCoolingPeriod(1);
        product.setTenure(10);
        product.setDescription("FD for tenure of 10 months , with interest rate 6.1 % .");

//        Mock behavior for Save
        when(productRepository.save(product)).thenReturn(product);

        // Mock behavior for findById
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));


        ProductEntity savedProduct = productRepository.save(product);
        Optional<ProductEntity> foundProduct = productRepository.findById(productId);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getProductId()).isEqualTo("FD01");
        assertThat(foundProduct.get().getTenure()).isEqualTo(10);
        assertThat(foundProduct.get().getInterestRate()).isEqualTo(6.1);
    }

    @Test
    void testFindById_NotFound() {

        String productId = "0001";

        // Mock behavior: return empty when randomId is used
        when(productRepository.findByProductId(productId)).thenReturn(null);

        Optional<ProductEntity> foundProduct = Optional.ofNullable(productRepository.findByProductId(productId));

        assertThat(foundProduct).isEmpty();
    }

}
