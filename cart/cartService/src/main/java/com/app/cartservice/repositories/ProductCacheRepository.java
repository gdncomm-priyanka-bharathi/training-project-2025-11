package com.app.cartservice.repositories;

import com.app.cartservice.cache.ProductCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCacheRepository extends MongoRepository<ProductCache, String> {
}
