package com.app.productservice.loader;

import com.app.productservice.entity.Product;
import com.app.productservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDataLoader implements CommandLineRunner {
    private final ProductRepository repo;
    private final StringRedisTemplate redis;

    public ProductDataLoader(ProductRepository repo, StringRedisTemplate redis) {
        this.repo = repo;
        this.redis = redis;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Only Redis flag decides seeding
            if ("true".equals(redis.opsForValue().get("product_seed_done"))) {
                System.out.println("Product seed already completed. Skipping load.");
                return;
            }

            List<Product> list = new ArrayList<>(50000);

            for (int i = 1; i <= 50000; i++) {
                Product p = new Product();
                p.setName("iphone " + i);
                p.setDescription("Description for product " + i);
                p.setPrice(Math.random() * 1000);
                p.setCategory("Category " + (i % 10));

                list.add(p);

                if (list.size() == 2000) {
                    repo.saveAll(list);
                    list.clear();
                }
            }

            if (!list.isEmpty()) {
                repo.saveAll(list);
            }

            redis.opsForValue().set("product_seed_done", "true");

            System.out.println("Inserted 50,000 products successfully.");
        } catch (Exception e) {
            redis.delete("product_seed_done"); // rollback flag
            throw e;
        }
    }
}
