package com.example.urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @MockBean
    private UrlRepository urlRepository;

    private Url url;

    @BeforeEach
    public void setup() {
        url = new Url("https://www.example.com", "abc123", 60);
    }

    @Test
    public void testCreateShortUrl() {
        when(urlRepository.existsByShortId("abc123")).thenReturn(false);
        when(urlRepository.save(Mockito.any(Url.class))).thenReturn(url);

        Url createdUrl = urlService.createShortUrl(url.getOriginalUrl(), url.getShortId(), url.getTtl());

        assertEquals("abc123", createdUrl.getShortId());
        assertEquals("https://www.example.com", createdUrl.getOriginalUrl());
    }

    @Test
    public void testGetUrlByShortId_NotExpired() {
        when(urlRepository.findByShortId("abc123")).thenReturn(Optional.of(url));

        Optional<Url> result = urlService.getUrlByShortId("abc123");

        assertTrue(result.isPresent());
        assertEquals("https://www.example.com", result.get().getOriginalUrl());
    }

    @Test
    public void testGetUrlByShortId_Expired() {
        url.setTtl(-10); // Set TTL to -10 seconds to simulate expiration
        when(urlRepository.findByShortId("abc123")).thenReturn(Optional.of(url));

        Optional<Url> result = urlService.getUrlByShortId("abc123");

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteUrlByShortId() {
        when(urlRepository.findByShortId("abc123")).thenReturn(Optional.of(url));

        urlService.deleteUrlByShortId("abc123");

        verify(urlRepository, times(1)).deleteByShortId("abc123");
    }
}