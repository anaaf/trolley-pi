CREATE TABLE IF NOT EXISTS tbl_product  (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         weight DECIMAL(10, 2) NOT NULL,
                         weight_unit INTEGER NOT NULL,
                         category INTEGER NOT NULL,
                        uuid varchar(36) NOT NULL UNIQUE ,
                         creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         created_by VARCHAR(255),
                         last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_by VARCHAR(255),
                         enabled BOOLEAN DEFAULT TRUE,
                         version INT DEFAULT 0
);