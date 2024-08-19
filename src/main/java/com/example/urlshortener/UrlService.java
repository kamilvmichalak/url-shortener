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
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortId(shortId);
        url.setTtl(ttl);
        return urlRepository.save(url);
    }

    public Optional<Url> getUrlByShortId(String shortId) {
        return Optional.ofNullable(urlRepository.findByShortId(shortId));
    }

    public void deleteUrlByShortId(String shortId) {
        urlRepository.deleteById(getUrlByShortId(shortId).orElseThrow().getId());
    }
}
