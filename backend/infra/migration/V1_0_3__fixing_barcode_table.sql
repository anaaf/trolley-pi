ALTER TABLE tbl_barcode
    ADD CONSTRAINT fk_product_uuid
        FOREIGN KEY (product_id)
            REFERENCES tbl_product(uuid);