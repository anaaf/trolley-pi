package com.crunch.cart.converter;

import com.crunch.cart.enums.TrolleyTypes;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrolleyTypeConverter implements AttributeConverter<TrolleyTypes, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TrolleyTypes status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public TrolleyTypes convertToEntityAttribute(Integer dbData) {
        return dbData != null ? TrolleyTypes.fromCode(dbData) : null;
    }
}
