package com.crunch.common.entity;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Data;
import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class CrunchEntity {

    @Column(name="uuid")
    protected String uuid = UUID.randomUUID().toString();

    @CreationTimestamp
    @Column(name = "creation_date")
    protected Timestamp creationDate;

    @Column(name="created_by")
    protected String createdBy;

    @UpdateTimestamp
    @Column(name = "last_updated")
    protected Timestamp lastUpdated;

    @Column(name="updated_by")
    protected String updatedBy;

    @Column(name="enabled")
    protected boolean enabled = true;

    @Version
    protected int version;

}
