package ai.meteoros.training.expenses.service;

import ai.meteoros.training.expenses.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private List<Product> products;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("products.json").getInputStream()) {
            products = mapper.readValue(is, new TypeReference<List<Product>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load products.json", e);
        }
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        return products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
}
