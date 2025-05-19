package com.crunch.barcode.service;

import com.crunch.barcode.entity.Barcode;
import com.crunch.barcode.entity.Product;
import com.crunch.barcode.repo.BarcodeRepository;
import com.crunch.barcode.repo.ProductRepository;
import com.crunch.common.enums.ProductCategory;
import com.crunch.common.enums.Weight;
import com.crunch.common.exceptions.EntityNotFoundException;
import com.crunch.common.validations.EntityValidation;
import com.crunch.user.model.BarcodeRequest;
import com.crunch.user.model.BarcodeResponse;
import com.crunch.user.model.ProductRequest;
import com.crunch.user.model.ProductResponse;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.crunch.common.error.ErrorType.BARCODE_NOT_FOUND;
import static com.crunch.common.error.ErrorType.ENTITY_ALREADY_EXISTS;
import static com.crunch.common.error.ErrorType.PRODUCT_NOT_FOUND;
import static com.crunch.common.validations.EntityValidation.validateEntityAlreadyExists;

@Data
@Service
public class BarcodeService {

    @Autowired
    BarcodeRepository barcodeRepository;

    @Autowired
    ProductRepository productRepository;

    public BarcodeResponse addBarcode(BarcodeRequest barcodeRequest) throws EntityNotFoundException {
        boolean doesExist = barcodeRepository.existsByBarcodeNumberAndEnabled(barcodeRequest.getBarcodeNumber(), true);
        validateEntityAlreadyExists(doesExist, ENTITY_ALREADY_EXISTS, barcodeRequest.getBarcodeNumber(), null);
        return transform(barcodeRepository.save(transform(barcodeRequest)), null);
    }

    @Nullable
    public BarcodeResponse getBarcodeByBarcodeNumber(String barcodeNumber) {
        Barcode barcode = barcodeRepository.findByBarcodeNumberAndEnabled(barcodeNumber, true);
        EntityValidation.validateEntityNotFound(barcode, BARCODE_NOT_FOUND, barcodeNumber, null);
        return transform(barcode, barcode.getProduct());
    }

    public ProductResponse addProduct(ProductRequest productRequest) {
        boolean doesExist = productRepository.existsByNameAndEnabled(productRequest.getName(), true);
        validateEntityAlreadyExists(doesExist, ENTITY_ALREADY_EXISTS, productRequest.getName(), null);
        return transform(productRepository.save(transform(productRequest)));
    }

    @Nullable
    public ProductResponse getProductByUuid(String productUuid) {
        Product product = productRepository.findByUuidAndEnabled(productUuid, true);
        EntityValidation.validateEntityNotFound(product, PRODUCT_NOT_FOUND, productUuid.toString(), null);
        return transform(product);
    }

    private Barcode transform(BarcodeRequest barcodeRequest) {
        Barcode barcode = new Barcode();
        barcode.setBarcodeNumber(barcodeRequest.getBarcodeNumber());
        barcode.setProductUuid(barcodeRequest.getProductUuid());
        return barcode;
    }

    private Product transform(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setWeight(productRequest.getWeight());
        product.setWeightUnit(Weight.valueOf(productRequest.getWeightUnit()));
        product.setPrice(productRequest.getPrice());
        return product;
    }

    public ProductResponse transform(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setName(product.getName());
        productResponse.setCategory(product.getCategory());
        productResponse.setWeight(product.getWeight());
        productResponse.setUuid(product.getUuid());
        return productResponse;
    }

    private BarcodeResponse transform(Barcode barcode, Product product) {
        BarcodeResponse barcodeResponse = new BarcodeResponse();
        barcodeResponse.setUuid(barcode.getUuid());
        barcodeResponse.setBarcodeNumber(barcode.getBarcodeNumber());
        barcodeResponse.setProductUuid(null);

        if(product != null) {
            barcodeResponse.setProductName(product.getName());
            barcodeResponse.setProductWeight(product.getWeight());
            barcodeResponse.setProductWeightUnit(product.getWeightUnit().toString());
        }
        return barcodeResponse;
    }
}

