package com.crunch.common.converter;

import com.crunch.common.enums.Status;
import com.crunch.common.enums.Weight;
import jakarta.persistence.AttributeConverter;

public class StatusConverter implements AttributeConverter<Status, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Status status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public Status convertToEntityAttribute(Integer dbData) {
        return dbData != null ? Status.fromCode(dbData) : null;
    }
}
