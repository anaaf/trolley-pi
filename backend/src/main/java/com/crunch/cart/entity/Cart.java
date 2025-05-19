package com.crunch.cart.entity;

import com.crunch.common.converter.StatusConverter;
import com.crunch.common.entity.CrunchEntity;
import com.crunch.common.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity(name = "tbl_cart")
public class Cart extends CrunchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "trolley_uuid")
    private String trolleyUuid;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    private Status status;

    @Column(name = "weight")
    private BigDecimal weight = BigDecimal.ZERO;

}
