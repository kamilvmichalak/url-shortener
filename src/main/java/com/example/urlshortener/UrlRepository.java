package com.example.urlshortener;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url,Long> {
    Url findByShortId(String shortId);
}
