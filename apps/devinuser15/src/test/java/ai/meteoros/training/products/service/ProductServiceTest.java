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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(1L, "Laptop", new BigDecimal("999.99"), "Electronics");
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Test
    void returns_all_products_when_repository_has_data() {
        Product second = new Product(2L, "Phone", new BigDecimal("599.99"), "Electronics");
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct, second));

        List<Product> result = productService.findAll();

        assertThat(result).hasSize(2).containsExactly(sampleProduct, second);
        verify(productRepository).findAll();
    }

    @Test
    void returns_empty_list_when_repository_is_empty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = productService.findAll();

        assertThat(result).isEmpty();
        verify(productRepository).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────────

    @Test
    void returns_product_when_id_exists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.findById(1L);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    void throws_ProductNotFoundException_when_id_does_not_exist_findById() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository).findById(99L);
    }

    // ── create ───────────────────────────────────────────────────────────

    @Test
    void saves_and_returns_product_with_generated_id() {
        Product input = new Product(null, "Tablet", new BigDecimal("399.99"), "Electronics");
        Product saved = new Product(3L, "Tablet", new BigDecimal("399.99"), "Electronics");
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.create(input);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Tablet");
        verify(productRepository).save(input);
    }

    @Test
    void clears_id_field_before_saving_when_id_is_passed() {
        Product input = new Product(50L, "Monitor", new BigDecimal("249.99"), "Electronics");
        Product saved = new Product(4L, "Monitor", new BigDecimal("249.99"), "Electronics");
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        productService.create(input);

        assertThat(input.getId()).isNull();
        verify(productRepository).save(input);
    }

    // ── update ───────────────────────────────────────────────────────────

    @Test
    void updates_existing_product_and_returns_updated() {
        Product updated = new Product(1L, "Laptop Pro", new BigDecimal("1299.99"), "Electronics");
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        Product result = productService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("1299.99"));
        verify(productRepository).existsById(1L);
        verify(productRepository).save(updated);
    }

    @Test
    void throws_ProductNotFoundException_when_id_does_not_exist_update() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.update(99L, sampleProduct))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository).existsById(99L);
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Test
    void deletes_existing_product() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void throws_ProductNotFoundException_when_id_does_not_exist_delete() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository).existsById(99L);
    }
}
