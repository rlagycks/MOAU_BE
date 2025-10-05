package com.moau.moau.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.Instant;

@MappedSuperclass
@Getter
public abstract class BaseSoftDelete extends BaseId {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected void markDeleted(Instant when) {
        this.deletedAt = when;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}