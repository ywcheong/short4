package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.repository.ShortURLRepository;
import com.ywcheong.short4.utility.ShortURLGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultReserveService implements ReserveService {

    private final ShortURLGenerator shortURLGenerator;
    private final ShortURLRepository shortURLRepository;
    @Value("${short4.server.max-url-generate-attempt}")
    private int maxUrlGenerateAttempt;

    @Autowired
    public DefaultReserveService(ShortURLGenerator shortURLGenerator, ShortURLRepository shortURLRepository) {
        this.shortURLGenerator = shortURLGenerator;
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public String reserveNewShortURL(String language) {
        for (int attempt = 0; attempt < maxUrlGenerateAttempt; attempt++) {
            String generatedURL = shortURLGenerator.generate(language);
            if (!isShortURLConflict(generatedURL)) {
                return generatedURL;
            }
        }

        log.error("Reserve failed within [{}] attempts", maxUrlGenerateAttempt);
        throw new RuntimeException("Failed to generate non-conflict ShortURL within configured attempts");
    }

    private boolean isShortURLConflict(String url) {
        return shortURLRepository.existsByShortURL(url);
    }
}
