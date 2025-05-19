package com.crunch.common.converter;

import com.crunch.common.enums.ProductCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductCategoryConverter  implements AttributeConverter<ProductCategory, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProductCategory status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public ProductCategory convertToEntityAttribute(Integer dbData) {
        return dbData != null ? ProductCategory.fromCode(dbData) : null;
    }
}

