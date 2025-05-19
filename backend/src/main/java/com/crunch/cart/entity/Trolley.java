package com.crunch.cart.entity;

import com.crunch.cart.converter.TrolleyTypeConverter;
import com.crunch.cart.enums.TrolleyTypes;
import com.crunch.common.converter.StatusConverter;
import com.crunch.common.converter.WeightConverter;
import com.crunch.common.entity.CrunchEntity;
import com.crunch.common.enums.Status;
import com.crunch.common.enums.Weight;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "tbl_trolley")
@Data
public class Trolley extends CrunchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "unit")
    @Convert(converter = WeightConverter.class)
    private Weight unit = Weight.KILOGRAM;

    @Column(name = "trolley_type")
    @Convert(converter = TrolleyTypeConverter.class)
    private TrolleyTypes trolleyType;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    private Status status;

}
