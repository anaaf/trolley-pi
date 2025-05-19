package com.crunch.barcode.controller;

import com.crunch.barcode.service.BarcodeService;
import com.crunch.user.api.ProductApi;
import com.crunch.user.model.ProductRequest;
import com.crunch.user.model.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/")
@RestController
public class ProductController implements ProductApi {

    @Autowired
    private BarcodeService barcodeService;

    public ProductController(BarcodeService barcodeService) {
    }

    @Override
    public ResponseEntity<ProductResponse> addProduct(ProductRequest productRequest) {

        ProductResponse response = barcodeService.addProduct(productRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ProductResponse> getProduct(String productUuid) {
        return null;
    }
}
