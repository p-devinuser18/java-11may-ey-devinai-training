package ai.meteoros.training.products.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ai.meteoros.training.products.entity.Product;
import ai.meteoros.training.products.exception.ProductNotFoundException;
import ai.meteoros.training.products.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Wireless Mouse", new BigDecimal("29.99"), "Electronics");
        product2 = new Product(2L, "Coffee Mug", new BigDecimal("12.50"), "Kitchen");
    }

    // ---- findAll ----

    @Test
    void returns_all_products_when_repository_has_data() {
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> result = productService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(product1, product2);
        verify(productRepository).findAll();
    }

    @Test
    void returns_empty_list_when_repository_is_empty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = productService.findAll();

        assertThat(result).isEmpty();
        verify(productRepository).findAll();
    }

    // ---- findById ----

    @Test
    void returns_product_when_id_exists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product result = productService.findById(1L);

        assertThat(result).isEqualTo(product1);
        assertThat(result.getName()).isEqualTo("Wireless Mouse");
        verify(productRepository).findById(1L);
    }

    @Test
    void throws_ProductNotFoundException_when_id_does_not_exist_findById() {
        when(productRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(9999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found: 9999");
        verify(productRepository).findById(9999L);
    }

    // ---- create ----

    @Test
    void saves_and_returns_product_with_generated_id() {
        Product input = new Product(null, "Keyboard", new BigDecimal("49.99"), "Electronics");
        Product saved = new Product(4L, "Keyboard", new BigDecimal("49.99"), "Electronics");
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.create(input);

        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("Keyboard");
        verify(productRepository).save(input);
    }

    @Test
    void clears_id_field_before_saving_when_id_is_passed() {
        Product input = new Product(99L, "Keyboard", new BigDecimal("49.99"), "Electronics");
        Product saved = new Product(4L, "Keyboard", new BigDecimal("49.99"), "Electronics");
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.create(input);

        assertThat(input.getId()).isNull();
        assertThat(result.getId()).isEqualTo(4L);
        verify(productRepository).save(input);
    }

    // ---- update ----

    @Test
    void updates_existing_product_and_returns_updated() {
        Product input = new Product(null, "Updated Mouse", new BigDecimal("34.99"), "Electronics");
        Product saved = new Product(1L, "Updated Mouse", new BigDecimal("34.99"), "Electronics");
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.update(1L, input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Mouse");
        assertThat(input.getId()).isEqualTo(1L);
        verify(productRepository).existsById(1L);
        verify(productRepository).save(input);
    }

    @Test
    void throws_ProductNotFoundException_when_id_does_not_exist_update() {
        Product input = new Product(null, "Updated Mouse", new BigDecimal("34.99"), "Electronics");
        when(productRepository.existsById(9999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.update(9999L, input))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found: 9999");
        verify(productRepository).existsById(9999L);
        verify(productRepository, never()).save(any());
    }

    // ---- delete ----

    @Test
    void deletes_existing_product() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void throws_ProductNotFoundException_when_id_does_not_exist_delete() {
        when(productRepository.existsById(9999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(9999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found: 9999");
        verify(productRepository).existsById(9999L);
        verify(productRepository, never()).deleteById(any());
    }
}
