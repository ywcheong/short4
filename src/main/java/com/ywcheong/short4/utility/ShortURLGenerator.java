package com.ywcheong.short4.utility;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
@Slf4j
public class ShortURLGenerator {
    private final TokenDictionary tokenDictionary;

    @Value("${short4.token.length}")
    private int keywordLength;

    @Autowired
    public ShortURLGenerator(TokenDictionary tokenDictionary) {
        this.tokenDictionary = tokenDictionary;
    }

    public @Nullable String generate(String language) {
        if (!tokenDictionary.isSupportedLanguage(language)) {
            log.error("Language [{}] not found", language);
            return null;
        }

        StringJoiner joiner = new StringJoiner("+");
        for (int i = 0; i < keywordLength; i++) {
            joiner.add(tokenDictionary.pickRandomToken(language));
        }

        String result = joiner.toString();
        log.info("Generated [{}]", result);
        return result;
    }
}
