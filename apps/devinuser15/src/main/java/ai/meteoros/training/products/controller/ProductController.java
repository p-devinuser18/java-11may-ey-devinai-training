package ai.meteoros.training.products.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.meteoros.training.products.model.Product;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final List<Product> products;

    public ProductController() {
        this.products = List.of(
                new Product(1L, "Wireless Mouse", new BigDecimal("29.99"), "Electronics"),
                new Product(2L, "Java Programming Book", new BigDecimal("49.95"), "Books"),
                new Product(3L, "Standing Desk", new BigDecimal("399.00"), "Furniture")
        );
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return products;
    }
}
