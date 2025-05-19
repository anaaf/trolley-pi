package com.crunch.common.converter;

import com.crunch.common.enums.Weight;
import jakarta.persistence.AttributeConverter;

public class WeightConverter implements AttributeConverter<Weight, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Weight status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public Weight convertToEntityAttribute(Integer dbData) {
        return dbData != null ? Weight.fromCode(dbData) : null;
    }
}
