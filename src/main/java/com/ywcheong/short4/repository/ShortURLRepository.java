package com.ywcheong.short4.repository;

 import com.ywcheong.short4.data.dto.ActivateResult;
import com.ywcheong.short4.data.entity.ShortURL;

public interface ShortURLRepository {
    boolean tryReserveThenResult(String token);

    ShortURL publish(ShortURL shortURL);

    ActivateResult activate(String token, String manageSecretHash);
}
