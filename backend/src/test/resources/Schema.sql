DROP table if exists tbl_user;
DROP table if exists tbl_group_detail;
DROP table if exists tbl_group;

CREATE TABLE tbl_user (
                          id SERIAL PRIMARY KEY,
                          user_name VARCHAR(50) NOT NULL ,
                          password VARCHAR(128),
                          email VARCHAR(255) NOT NULL,
                          phone_no VARCHAR(255) NOT NULL,
                          uuid UUID NOT NULL UNIQUE ,
                          creation_date TIMESTAMP,
                          created_by VARCHAR(255),
                          last_updated TIMESTAMP,
                          updated_by VARCHAR(255),
                          enabled BOOLEAN NOT NULL ,
                          version INT,
                            UNIQUE (uuid)
);

CREATE TABLE IF NOT EXISTS tbl_group (
                                         name VARCHAR(50),
                                         balance DECIMAL(20,3),
                                         uuid UUID NOT NULL UNIQUE,
                                         id SERIAL PRIMARY KEY,
                                         creation_date TIMESTAMP,
                                         created_by VARCHAR(255),
                                         last_updated TIMESTAMP,
                                         updated_by VARCHAR(255),
                                         enabled BOOLEAN NOT NULL ,
                                         version INT,
                                             UNIQUE (uuid)
);


CREATE TABLE IF NOT EXISTS tbl_group_detail (
                                                group_uuid UUID NOT NULL,
                                                member_uuid UUID NOT NULL,
                                                balance DECIMAL(20,3),
                                                uuid UUID NOT NULL UNIQUE,
                                                id SERIAL PRIMARY KEY ,
                                                creation_date TIMESTAMP,
                                                created_by VARCHAR(255),
                                                last_updated TIMESTAMP,
                                                updated_by VARCHAR(255),
                                                enabled BOOLEAN NOT NULL ,
                                                version INT,
                                                UNIQUE (group_uuid, member_uuid),
                                                FOREIGN KEY (group_uuid) REFERENCES tbl_group(uuid),
                                                FOREIGN KEY (member_uuid) REFERENCES tbl_user(uuid)
);


