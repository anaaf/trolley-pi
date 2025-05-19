package com.crunch.cart.enums;

public enum TrolleyTypes {

    BARCODE_SCANNING(1),
    CAMERA_PLUS_BARCODE(2),
    CAMERA_360(3);


    private int sequence;

    TrolleyTypes(int sequence) {
        this.sequence = sequence;
    }

    public int getCode() {
        return sequence;
    }

    public static TrolleyTypes fromCode(int code) {
        for (TrolleyTypes category : TrolleyTypes.values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid Trolley category :" + code);
    }

}
