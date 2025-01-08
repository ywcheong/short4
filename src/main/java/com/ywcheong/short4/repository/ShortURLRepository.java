package com.ywcheong.short4.repository;

import com.ywcheong.short4.data.entity.ShortURL;

public interface ShortURLRepository {
    boolean attemptReserve(String token);

    ShortURL publish(ShortURL shortURL);

    boolean activate(String token, String manageSecretHash);
}
