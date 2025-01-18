package com.ywcheong.short4.service.redirect;

import com.ywcheong.short4.data.dto.redirect.RedirectResult;
import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.data.types.RedirectResultType;
import com.ywcheong.short4.repository.ShortURLRepository;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class DefaultRedirectService implements RedirectService {
    private final ShortURLRepository shortURLRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultRedirectService(ShortURLRepository shortURLRepository, PasswordEncoder passwordEncoder) {
        this.shortURLRepository = shortURLRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public @NotNull RedirectResult redirect(@NotNull String token, @Nullable String accessSecret) {
        ShortURL shortURL = shortURLRepository.findByToken(token);

        if (shortURL == null) {
            log.info("Redirect Service :: token not exists :: token [{}]", token);
            return new RedirectResult(RedirectResultType.TOKEN_NOT_FOUND);
        }

        if (!isSecretMatchWithHash(accessSecret, shortURL.getAccessSecretHash())) {
            log.info("Redirect Service :: token exists, but wrong access secret :: token [{}] ShortURL [{}]", token, shortURL);
            return new RedirectResult(RedirectResultType.WRONG_ACCESS_SECRET);
        }

        try {
            URI originalURI = new URI(shortURL.getOriginalURL());
            log.info("Redirect Service :: token exists, redirecting :: token [{}] ShortURL [{}]", token, shortURL);
            return new RedirectResult(RedirectResultType.SUCCESS, originalURI);
        } catch (URISyntaxException e) {
            log.error("Redirect Service :: DB-stored originalURL for given token is invalid URI, which violates HTTPKindURL :: token [{}] ShortURL [{}]", token, shortURL);
            throw new RuntimeException("DB-stored originalURL for given token is invalid URI, which violates HTTPKindURL");
        }
    }

    private boolean isSecretMatchWithHash(@Nullable String accessSecret, @Nullable String accessSecretHash) {
        if (accessSecretHash == null)
            return true;
        if (accessSecret == null)
            return false;
        return passwordEncoder.matches(accessSecret, accessSecretHash);
    }
}
