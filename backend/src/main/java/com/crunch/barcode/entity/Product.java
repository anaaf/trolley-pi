package com.crunch.barcode.entity;

import com.crunch.common.converter.ProductCategoryConverter;
import com.crunch.common.converter.WeightConverter;
import com.crunch.common.entity.CrunchEntity;
import com.crunch.common.enums.ProductCategory;
import com.crunch.common.enums.Weight;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity(name = "tbl_product")
public class Product extends CrunchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "weight_unit")
    @Convert(converter = WeightConverter.class)
    private Weight weightUnit;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "category")
    @Convert(converter = ProductCategoryConverter.class)
    private ProductCategory category;

}
