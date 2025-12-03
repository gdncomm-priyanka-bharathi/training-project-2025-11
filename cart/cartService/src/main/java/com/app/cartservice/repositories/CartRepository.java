package com.app.cartservice.repositories;

import com.app.cartservice.entity.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartRepository extends MongoRepository<ShoppingCart, String> {

}
