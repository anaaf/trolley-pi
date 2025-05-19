package com.crunch.common.exceptions;

import com.crunch.common.error.ErrorType;
import lombok.ToString;

@ToString
public class EntityNotFoundException extends BaseException {

    private final String uuid;
    private final ErrorType errorCode;

    private final String requestUuid;

    public EntityNotFoundException(ErrorType errorCode, String uuid, String requestUuid) {
        super(errorCode.getMessage()+String.format(" for uuid %s" ,uuid));
        this.uuid = uuid;
        this.errorCode = errorCode;
        this.requestUuid = requestUuid;
    }

    @Override
    public int getErrorCode() {
        return errorCode.getCode();
    }
}
