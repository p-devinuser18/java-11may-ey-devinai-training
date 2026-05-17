package ai.meteoros.training.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ai.meteoros.training.products.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
