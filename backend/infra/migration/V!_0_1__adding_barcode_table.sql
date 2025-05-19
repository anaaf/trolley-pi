CREATE TABLE tbl_barcode (
                         id SERIAL PRIMARY KEY,
                         barcode_number VARCHAR(255),
                         product_uuid varchar(36) REFERENCES tbl_product(uuid),
                         uuid varchar(36) UNIQUE NOT NULL,
                         creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         created_by VARCHAR(255),
                         last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_by VARCHAR(255),
                         enabled BOOLEAN DEFAULT TRUE,
                         version INTEGER DEFAULT 0
);