package com.ywcheong.short4.repository;

import com.ywcheong.short4.data.entity.ShortURL;
import org.springframework.data.repository.Repository;

public interface ShortURLRepository extends Repository<ShortURL, String> {
    boolean existsByShortURL(String shortURL);
}
