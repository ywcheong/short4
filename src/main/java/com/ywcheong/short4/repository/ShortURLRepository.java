package com.ywcheong.short4.repository;

import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.data.types.ActivateResultType;

public interface ShortURLRepository {
    boolean tryReserveThenResult(String token);

    ShortURL publish(ShortURL shortURL);

    ActivateResultType activate(String token, String manageSecretHash);
}
