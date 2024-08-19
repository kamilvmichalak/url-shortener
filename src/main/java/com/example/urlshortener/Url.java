package com.example.urlshortener;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String shortId;
    private Integer ttl; // time-to-live in seconds
    private LocalDateTime createdAt;

    public Url() {
        this.createdAt = LocalDateTime.now();
    }

    public Url(String originalUrl, String shortId, Integer ttl) {
        this.originalUrl = originalUrl;
        this.shortId = shortId;
        this.ttl = ttl;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        if (ttl == 0) return false;
        return createdAt.plusSeconds(ttl).isBefore(LocalDateTime.now());
    }
}