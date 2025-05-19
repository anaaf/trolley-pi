package com.crunch.common.validations;

import com.crunch.common.entity.CrunchEntity;
import com.crunch.common.error.ErrorType;
import com.crunch.common.exceptions.EntityAlreadyExistsException;
import com.crunch.common.exceptions.EntityNotFoundException;

import static com.crunch.common.error.ErrorType.ENTITY_ALREADY_EXISTS;

public class EntityValidation {

    public static void validateEntityNotFound(CrunchEntity entity, ErrorType type, String identifier, String requestUuid) {
        if(entity == null) {
            throw new EntityNotFoundException(type, identifier, requestUuid);
        }
    }

    public static void validateEntityAlreadyExists(boolean doesExist, ErrorType type, String identifier, String requestUuid) {
        if(doesExist) {
            throw new EntityAlreadyExistsException(type, identifier, requestUuid);
        }
    }

}
