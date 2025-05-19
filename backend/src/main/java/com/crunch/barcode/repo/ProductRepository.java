package com.crunch.barcode.repo;

import com.crunch.barcode.entity.Product;
import com.crunch.common.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

     Product findByUuidAndEnabled(String uuid, boolean enabled);
     boolean existsByNameAndEnabled(String name, boolean enabled);
}
