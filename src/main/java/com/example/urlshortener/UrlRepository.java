package com.example.urlshortener;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortId(String shortId);
    boolean existsByShortId(String shortId);
    void deleteByShortId(String shortId);
}