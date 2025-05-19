package com.crunch.common.enums;

public enum Weight {

    KILOGRAM(1),
    GRAM(2);

    private final int sequence;

    Weight(int sequence) {
        this.sequence = sequence;
    }

    public int getCode() {
        return sequence;
    }

    public static Weight fromCode(int code) {
        for (Weight category : Weight.values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid weight :" + code);
    }
}
