package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.repository.ShortURLRepository;
import com.ywcheong.short4.utility.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultReserveService implements ReserveService {
    private final TokenGenerator tokenGenerator;
    private final ShortURLRepository shortURLRepository;
    @Value("${short4.server.max-url-generate-attempt}")
    private int maxUrlGenerateAttempt;

    @Autowired
    public DefaultReserveService(TokenGenerator tokenGenerator, ShortURLRepository shortURLRepository) {
        this.tokenGenerator = tokenGenerator;
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public String reserveToken(String language) {
        for (int attempt = 0; attempt < maxUrlGenerateAttempt; attempt++) {
            String generatedToken = tokenGenerator.generate(language);
            if (shortURLRepository.attemptReserve(generatedToken)) {
                return generatedToken;
            }
        }

        log.error("Reserve Service :: reserve failed within attempts :: attempts [{}]", maxUrlGenerateAttempt);
        throw new RuntimeException("reserve failed within attempts");
    }
}
