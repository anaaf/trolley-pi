DROP TABLE IF EXISTS tbl_trolley CASCADE ;
DROP TABLE IF EXISTS tbl_cart CASCADE ;
DROP TABLE IF EXISTS tbl_cart_item CASCADE ;

CREATE TABLE tbl_trolley (
                             id SERIAL PRIMARY KEY,
                             capacity INTEGER NOT NULL,
                             unit INTEGER NOT NULL,
                             trolley_type INTEGER NOT NULL,
                             status INTEGER NOT NULL,
                             uuid varchar(36) UNIQUE NOT NULL,
                             creation_date TIMESTAMP,
                             created_by VARCHAR(255),
                             last_updated TIMESTAMP,
                             updated_by VARCHAR(255),
                             enabled BOOLEAN NOT NULL ,
                             version INT
);

CREATE TABLE tbl_cart (
                          id SERIAL PRIMARY KEY,
                          trolley_uuid varchar(36) NOT NULL REFERENCES tbl_trolley(uuid),
                          status INTEGER NOT NULL,
                          uuid varchar(36) UNIQUE NOT NULL,
                          creation_date TIMESTAMP,
                          created_by VARCHAR(255),
                          last_updated TIMESTAMP,
                          updated_by VARCHAR(255),
                          enabled BOOLEAN NOT NULL ,
                          version INT
);

CREATE TABLE tbl_cart_item (
                          id SERIAL PRIMARY KEY,
                          cart_uuid varchar(36) NOT NULL REFERENCES tbl_cart(uuid),
                          product_uuid varchar(36) NOT NULL REFERENCES tbl_product(uuid),
                          quantity INT,
                          uuid varchar(36) UNIQUE NOT NULL,
                          creation_date TIMESTAMP,
                          created_by VARCHAR(255),
                          last_updated TIMESTAMP,
                          updated_by VARCHAR(255),
                          enabled BOOLEAN NOT NULL ,
                          version INT
);

