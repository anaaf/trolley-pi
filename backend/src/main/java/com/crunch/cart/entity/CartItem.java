package com.crunch.cart.entity;

import com.crunch.barcode.entity.Product;
import com.crunch.common.entity.CrunchEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "tbl_cart_item")
public class CartItem extends CrunchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "product_uuid")
    private String productUuid;

    @Column(name = "cart_uuid")
    private String cartUuid;

    @Column(name = "quantity")
    private int quantity = 1;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private Product product;

}
