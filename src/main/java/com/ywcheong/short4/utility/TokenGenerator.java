package com.ywcheong.short4.utility;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
@Slf4j
public class TokenGenerator {
    private final WordDictionary wordDictionary;

    @Value("${short4.token.word-count}")
    private int wordCount;

    @Autowired
    public TokenGenerator(WordDictionary wordDictionary) {
        this.wordDictionary = wordDictionary;
    }

    public @Nullable String generate(String language) {
        if (!wordDictionary.isSupportedLanguage(language)) {
            log.error("Token Generator :: language not found :: language [{}]", language);
            return null;
        }

        StringJoiner joiner = new StringJoiner("+");
        for (int i = 0; i < wordCount; i++) {
            joiner.add(wordDictionary.pickRandomToken(language));
        }

        String result = joiner.toString();
        log.info("Token Generator :: token generated :: token [{}]", result);
        return result;
    }
}
