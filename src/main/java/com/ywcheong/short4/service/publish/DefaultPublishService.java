package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.repository.ShortURLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
@Slf4j
public class DefaultPublishService implements PublishService {
    private final ReserveService reserveService;
    private final ShortURLRepository shortURLRepository;
    @Value("${short4.server.manage-secret-length}")
    private int manageSecretLength;

    @Autowired
    public DefaultPublishService(ReserveService reserveService, ShortURLRepository shortURLRepository) {
        this.reserveService = reserveService;
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public boolean publishURL(ShortURL publishShortURL) {
        String token = reserveService.reserveToken("en-US");
        publishShortURL.setToken(token);

        log.info("Publish Service :: token attached to ShortURL :: ShortURL [{}]", publishShortURL);
        return shortURLRepository.publish(publishShortURL);
    }

    @Override
    public String generateRandomManageSecret() {
        SecureRandom random = new SecureRandom();

        byte[] manageSecretBytes = new byte[manageSecretLength];
        random.nextBytes(manageSecretBytes);
        return new String(manageSecretBytes, StandardCharsets.UTF_8);
    }
}
