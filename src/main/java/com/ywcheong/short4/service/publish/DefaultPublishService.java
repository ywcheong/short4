package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.ActivateRequestDTO;
import com.ywcheong.short4.data.dto.ActivateResult;
import com.ywcheong.short4.data.dto.PublishRequestDTO;
import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.repository.ShortURLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Slf4j
public class DefaultPublishService implements PublishService {
    private static final String MANAGE_SECRET_CHAR_POOL = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final PasswordEncoder passwordEncoder;
    private final ReserveService reserveService;
    private final ShortURLRepository shortURLRepository;
    @Value("${short4.server.manage-secret-length}")
    private int manageSecretLength;

    @Autowired
    public DefaultPublishService(PasswordEncoder passwordEncoder, ReserveService reserveService, ShortURLRepository shortURLRepository) {
        this.passwordEncoder = passwordEncoder;
        this.reserveService = reserveService;
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public String publishURL(PublishRequestDTO requestDTO) {
        ShortURL publishShortURL = ShortURL.builder()
                .originalURL(requestDTO.getOriginalURL())
                .expireAfterSeconds(requestDTO.getExpireAfterSeconds())
                .expireAfterVisits(requestDTO.getExpireAfterVisits())
                .isForcefullyDowned(false)
                .build();

        // 토큰 처리
        String token = reserveService.reserveToken("en-US");
        publishShortURL.setToken(token);

        // accessSecret -> accessSecretHash 변환
        String accessSecretHash = computeAccessSecretHash(requestDTO.getAccessSecret());
        publishShortURL.setAccessSecretHash(accessSecretHash);

        // if (manage) -> manageSecretHash 생성
        String manageSecret;
        if (requestDTO.getIsUsingManage()) {
            manageSecret = createManageSecret();
            String manageSecretHash = passwordEncoder.encode(manageSecret);
            publishShortURL.setManageSecretHash(manageSecretHash);
            publishShortURL.setIsActivated(false);
        } else {
            manageSecret = null;
            publishShortURL.setManageSecretHash(null);
            publishShortURL.setIsActivated(true);
        }

        log.info("Publish Service -> ShortURL Repository  :: ShortURL [{}]", publishShortURL);
        shortURLRepository.publish(publishShortURL);
        return manageSecret;
    }

    @Override
    public ActivateResult activateURL(ActivateRequestDTO requestDTO) {
        String token = requestDTO.getToken();
        String manageSecret = requestDTO.getManageSecret();

        return shortURLRepository.activate(token, manageSecret);
    }

    private String computeAccessSecretHash(String accessSecret) {
        if (accessSecret.isEmpty()) {
            return null;
        }
        return passwordEncoder.encode(accessSecret);
    }

    public String createManageSecret() {
        SecureRandom random = new SecureRandom();

        return random.ints(manageSecretLength, 0, MANAGE_SECRET_CHAR_POOL.length())
                .mapToObj(MANAGE_SECRET_CHAR_POOL::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
