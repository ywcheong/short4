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
    public ShortURL publishURL(PublishRequestDTO requestDTO) {
        ShortURL publishShortURL = ShortURL.builder()
                .originalURL(requestDTO.getOriginalURL())
                .expireAfterSeconds(requestDTO.getExpireAfterSeconds())
                .expireAfterVisits(requestDTO.getExpireAfterVisits())
                .isForcefullyDowned(false)
                .build();

        // 토큰 처리
        String token = reserveService.reserveToken("en-US");
        publishShortURL.setToken(token);
        log.info("Publish Service :: reserved token attached :: ShortURL [{}]", publishShortURL);

        // accessSecret -> accessSecretHash 변환
        String accessSecretHash = computeAccessSecretHash(requestDTO.getAccessSecret());
        publishShortURL.setAccessSecretHash(accessSecretHash);

        // if (manage) -> manageSecretHash 생성
        if (requestDTO.getIsUsingManage()) {
            publishShortURL.setManageSecretHash(createManageSecretHash());
            publishShortURL.setIsActivated(false);
        } else {
            publishShortURL.setIsActivated(true);
        }

        return shortURLRepository.publish(publishShortURL);
    }

    @Override
    public ActivateResult activateURL(ActivateRequestDTO requestDTO) {
        String token = requestDTO.getToken();
        String manageSecret = requestDTO.getManageSecret();

        return shortURLRepository.activate(token, manageSecret);
    }

    public String computeAccessSecretHash(String accessSecret) {
        if (accessSecret.isEmpty()) {
            return null;
        }
        return passwordEncoder.encode(accessSecret);
    }

    public String createManageSecretHash() {
        String manageSecret = generateRandomManageSecret();
        return passwordEncoder.encode(manageSecret);
    }

    @Override
    public String generateRandomManageSecret() {
        SecureRandom random = new SecureRandom();

        return random.ints(manageSecretLength, 0, MANAGE_SECRET_CHAR_POOL.length())
                .mapToObj(MANAGE_SECRET_CHAR_POOL::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
