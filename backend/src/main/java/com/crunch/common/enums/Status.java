package com.crunch.common.enums;

public enum Status {

    ACTIVE(1),
    INACTIVE(2);

    private final int sequence;

    Status(int sequence) {
        this.sequence = sequence;
    }

    public int getCode() {
        return sequence;
    }

    public static Status fromCode(int code) {
        for (Status category : Status.values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid status :" + code);
    }
}
