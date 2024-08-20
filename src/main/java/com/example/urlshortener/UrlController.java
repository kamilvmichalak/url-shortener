package com.example.urlshortener;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlController {
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/create-short-url")
    public ResponseEntity<?> createShortUrl(@RequestParam("url") String originalUrl,
                                            @RequestParam("shortId") Optional<String> shortId,
                                            @RequestParam("ttl") Optional<Integer> ttl) {
        try {
            String generatedShortId = shortId.filter(s -> !s.isEmpty())
                    .orElseGet(() -> RandomStringUtils.randomAlphanumeric(6));

            Url url = urlService.createShortUrl(originalUrl, generatedShortId, ttl.orElse(0));
            logger.info("Short URL created: {}", generatedShortId);
            return new ResponseEntity<>(url, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating short URL: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<Void> redirectUrl(@PathVariable("shortId") String shortId) {
        Optional<Url> url = urlService.getUrlByShortId(shortId);
        if (url.isPresent()) {
            if (url.get().isExpired()) {
                logger.info("Short URL expired: {}", shortId);
                urlService.deleteUrlByShortId(shortId);
                return ResponseEntity.notFound().build();
            }
            logger.info("Redirecting to: {}", url.get().getOriginalUrl());
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url.get().getOriginalUrl())).build();
        } else {
            logger.warn("Short URL not found: {}", shortId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delete-short-url")
    public ResponseEntity<String> deleteUrl(@RequestParam("shortId") String shortId) {
        Optional<Url> url = urlService.getUrlByShortId(shortId);
        if (url.isPresent()) {
            urlService.deleteUrlByShortId(shortId);
            logger.info("Short URL deleted: {}", shortId);
            return ResponseEntity.ok("Short URL with ID " + shortId + " has been deleted successfully.");
        } else return ResponseEntity.ok("Short URL with ID " + shortId + " not found in the database!");

    }
}