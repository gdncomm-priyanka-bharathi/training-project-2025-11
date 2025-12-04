package com.app.cartservice.repositories;

import com.app.cartservice.entity.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends MongoRepository<ShoppingCart, String> {

    Optional<ShoppingCart> findByCustomerId(String customerI);
}
