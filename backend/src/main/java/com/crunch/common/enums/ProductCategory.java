package com.crunch.common.enums;

public enum ProductCategory {

    FMCG(1),
    STORE_PACKAGED(2),
    ACCESSORIES(3),
    ELECTRONICS(4),
    CLOTHING(5);


    private int sequence;

    ProductCategory(int sequence) {
        this.sequence = sequence;
    }

    public int getCode() {
        return sequence;
    }

    public static ProductCategory fromCode(int code) {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid Product category :" + code);
    }

}
