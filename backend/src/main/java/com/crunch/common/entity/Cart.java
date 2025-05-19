package com.crunch.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_cart")
@EqualsAndHashCode(callSuper = true)
public class Cart extends CrunchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight", nullable = false, precision = 13, scale = 4)
    private BigDecimal weight;

    @Column(name = "total", nullable = false, precision = 13, scale = 4)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "items")
    private Integer items;
} 