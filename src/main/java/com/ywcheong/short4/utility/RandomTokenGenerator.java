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
import java.util.*;

@Component
@Slf4j
@Getter
public class RandomTokenGenerator {
    private final int randomSeed = 1234; // Todo 서버 부팅 시간에 따른 시드 의존성 주입
    private final ResourceLoader resourceLoader;
    private final Map<String, List<String>> dictionaryOfLanguage = new HashMap<>();
    @Value("${short4.token-languages}")
    private String[] tokenLanguages;

    @Autowired
    public RandomTokenGenerator(@Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initRandomTokenGenerator() {
        loadDictionaries();
    }

    public void loadDictionaries() {
        // Properties에서 동적으로 로드하는 Token Language 설정이 제대로 들어왔는지 검사
        if (tokenLanguages == null) {
            log.error("Property [short4.token-languages] not defined");
            throw new RuntimeException("Property not defined");
        }
        log.info("Property [short4.token-languages] found [{}]", Arrays.toString(tokenLanguages));

        for (String language : tokenLanguages) {
            List<String> dictionary = loadDictionaryOfLanguage(language);
            dictionaryOfLanguage.put(language, dictionary);
        }
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
            log.error("Resource [{}] not found", dictionaryPath);
            throw new RuntimeException("Dictionary not found");
        }
        log.info("Resource [{}] found", dictionaryPath);

        // 해당 언어 사전을 로드해서 ArrayList<String>으로 변환
        String dictionaryContent;

        try {
            dictionaryContent = resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Resource [{}] IOException [{}]", dictionaryPath, e.toString());
            throw new RuntimeException(e);
        }

        return dictionaryContent;
    }
}
