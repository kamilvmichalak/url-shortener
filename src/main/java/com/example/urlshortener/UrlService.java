package com.example.urlshortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);


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

    @Transactional
    public void deleteUrlByShortId(String shortId) {
        urlRepository.deleteByShortId(shortId);
    }

    @Transactional // This ensures the method runs within a transaction
    @Scheduled(fixedRate = 60000) // Runs every minute
    public void deleteExpiredUrls() {
        List<Url> expiredUrls = urlRepository.findAll();
        for (Url url : expiredUrls) {
            if (url.isExpired()) {
                deleteUrlByShortId(url.getShortId());
                logger.info("Short URL with ID '{}' has been removed from the database.", url.getShortId());
            }
        }
    }

}