package com.example.urlshortener;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;
    private String shortId;
    private Integer ttl; // time-to-live in seconds

    public Url() {

    }

    public Url(String originalUrl, String shortId, Integer ttl) {
        this.originalUrl = originalUrl;
        this.shortId = shortId;
        this.ttl = ttl;
    }
}
