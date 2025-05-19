CREATE TABLE tbl_user (
          id SERIAL PRIMARY KEY,
          user_name VARCHAR(50) NOT NULL ,
          password VARCHAR(128),
          email VARCHAR(255) NOT NULL,
          phone_no VARCHAR(255) NOT NULL,
          uuid UUID NOT NULL,
          creation_date TIMESTAMP,
          created_by VARCHAR(255),
          last_updated TIMESTAMP,
          updated_by VARCHAR(255),
          enabled BOOLEAN NOT NULL ,
          version INT
);
