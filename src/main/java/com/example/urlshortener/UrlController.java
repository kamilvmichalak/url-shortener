package com.example.urlshortener;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlController {
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Url> createShortUrl(@RequestParam("url") String originalUrl, @RequestParam("shortId") Optional<String> shortId, @RequestParam("ttl") Optional<Integer> ttl) {
        String generatedShortId = shortId.orElseGet(() -> RandomStringUtils.randomAlphanumeric(6));
        Url url = urlService.createShortUrl(originalUrl, generatedShortId, ttl.orElse(0));
        return new ResponseEntity<>(url, HttpStatus.CREATED);
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<Void> redirectUrl(@PathVariable("shortId") String shortId) {
        Optional<Url> url = urlService.getUrlByShortId(shortId);
        if (url.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url.get().getOriginalUrl())).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{shortId}")
    public ResponseEntity<Void> deleteUrl(@PathVariable("shortId") String shortId) {
        urlService.deleteUrlByShortId(shortId);
        return ResponseEntity.noContent().build();
    }
}