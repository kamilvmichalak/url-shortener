package com.example.urlshortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Url createShortUrl(String originalUrl, String shortId, Integer ttl) {
        if (urlRepository.existsByShortId(shortId)) {
            throw new IllegalArgumentException("Short ID already in use: " + shortId);
        }
        Url url = new Url(originalUrl, shortId, ttl);
        return urlRepository.save(url);
    }

    public Optional<Url> getUrlByShortId(String shortId) {
        return urlRepository.findByShortId(shortId)
                .filter(url -> !url.isExpired());
    }

    public void deleteUrlByShortId(String shortId) {
        urlRepository.deleteByShortId(shortId);
    }
}