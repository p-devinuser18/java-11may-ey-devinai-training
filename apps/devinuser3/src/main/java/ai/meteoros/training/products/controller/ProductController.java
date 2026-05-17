package ai.meteoros.training.products.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.meteoros.training.products.model.Product;

@RestController
public class ProductController {

    private final List<Product> products;

    public ProductController() {
        this.products = List.of(
                new Product(1L, "Wireless Mouse", new BigDecimal("29.99"), "Electronics"),
                new Product(2L, "Coffee Mug", new BigDecimal("12.50"), "Kitchen"),
                new Product(3L, "Notebook", new BigDecimal("5.99"), "Stationery")
        );
    }

    @GetMapping("/api/products")
    public List<Product> getAllProducts() {
        return products;
    }
}
