package com.ms.microservices.respository;

import com.ms.microservices.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository  extends MongoRepository<Product,String> {
}
