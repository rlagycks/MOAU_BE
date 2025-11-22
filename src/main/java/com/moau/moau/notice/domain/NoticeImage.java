package com.moau.moau.notice.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notice_images")
public class NoticeImage extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(name = "image_key", nullable = false)
    private String imageKey; // S3 Key

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder
    public NoticeImage(Notice notice, String imageKey, int sortOrder) {
        this.notice = notice;
        this.imageKey = imageKey;
        this.sortOrder = sortOrder;
    }
}