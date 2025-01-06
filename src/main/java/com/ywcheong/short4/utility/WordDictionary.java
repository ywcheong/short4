package com.ywcheong.short4.utility;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

@Component
@Slf4j
public class WordDictionary {
    @Getter
    private Map<String, List<String>> dictionaryOfLanguage;

    @Getter
    @Value("${short4.token.languages}")
    private List<String> tokenLanguages;
    private final ResourceLoader resourceLoader;

    @Autowired
    public WordDictionary(@Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    private void initTokenDictionary() {
        if (!validateTokenLanguageProperty()) {
            throw new RuntimeException("undefined property");
        }

        this.dictionaryOfLanguage = loadDictionaries();
    }

    public boolean validateTokenLanguageProperty() {
        // Properties에서 동적으로 로드하는 Token Language 설정이 제대로 들어왔는지 검사
        if (tokenLanguages == null) {
            log.error("Word Dictionary :: undefined property `short4.token-languages`");
            return false;
        }

        log.info("Word Dictionary :: found property `short4.token-languages` :: value [{}]", tokenLanguages);
        return true;
    }

    public Map<String, List<String>> loadDictionaries() {
        // Properties에서 동적으로 로드하는 Token Language 설정이 제대로 들어왔는지 검사
        Map<String, List<String>> result = new HashMap<>();
        for (String language : tokenLanguages) {
            List<String> dictionary = loadDictionaryOfLanguage(language);
            result.put(language, dictionary);
        }
        return result;
    }

    public List<String> loadDictionaryOfLanguage(String language) {
        // resource 폴더에서 해당 언어 사전 검색
        String dictionaryPath = """
                classpath:dictionary/%s.dictionary.txt""".formatted(language);
        String dictionaryContent = loadDictionaryFile(dictionaryPath);
        return convertContentIntoDictionary(dictionaryContent);
    }

    public List<String> convertContentIntoDictionary(String dictionaryContent) {
        StringTokenizer tokenizer = new StringTokenizer(dictionaryContent, "\n");
        List<String> dictionary = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            dictionary.add(tokenizer.nextToken());
        }
        return dictionary;
    }

    public String loadDictionaryFile(String dictionaryPath) {
        Resource resource = resourceLoader.getResource(dictionaryPath);

        if (!resource.exists()) {
            log.error("Word Dictionary :: dictionary not found :: path [{}]", dictionaryPath);
            throw new RuntimeException("dictionary not found");
        }
        log.info("Word Dictionary :: dictionary found :: path [{}]", dictionaryPath);

        // 해당 언어 사전을 로드해서 ArrayList<String>으로 변환
        String dictionaryContent;

        try {
            dictionaryContent = resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info("Word Dictionary :: IOException :: path [{}] error [{}]", dictionaryPath, e.toString());
            throw new RuntimeException(e);
        }

        return dictionaryContent;
    }

    public Boolean isSupportedLanguage(String language) {
        return tokenLanguages.contains(language);
    }

    public String pickRandomToken(String language) {
        SecureRandom random = new SecureRandom();
        List<String> dictionary = dictionaryOfLanguage.get(language);
        return dictionary.get(random.nextInt(dictionary.size()));
    }
}
