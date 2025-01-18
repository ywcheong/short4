package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.data.dto.publish.ActivateRequest;
import com.ywcheong.short4.data.dto.publish.ActivateResult;
import com.ywcheong.short4.data.dto.publish.PublishRequest;
import com.ywcheong.short4.data.dto.publish.PublishResult;
import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.data.types.ActivateResultType;
import com.ywcheong.short4.repository.ShortURLRepository;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Slf4j
public class DefaultPublishService implements PublishService {
    // 비밀번호 풀 (i, I, l, L, 1, 0, o, O 제외됨)
    private static final String MANAGE_SECRET_CHAR_POOL = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    // 의존성 주입
    private final PasswordEncoder passwordEncoder;
    private final ReserveService reserveService;
    private final ShortURLRepository shortURLRepository;
    // 외부 설정
    @Value("${short4.server.manage-secret-length}")
    private int manageSecretLength;

    @Autowired
    public DefaultPublishService(PasswordEncoder passwordEncoder,
                                 ReserveService reserveService,
                                 ShortURLRepository shortURLRepository) {
        this.passwordEncoder = passwordEncoder;
        this.reserveService = reserveService;
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public @NotNull PublishResult publishURL(@NotNull PublishRequest request) {
        // token 발행
        String token = reserveService.reserveToken("en-US");

        // accessSecret & manageSecret 설정
        String accessSecret = computeAccessSecret(request);
        String manageSecret = computeManangeSecret(request);

        // secret 해시처리
        String accessSecretHash = computeHashedSecret(accessSecret);
        String manageSecretHash = computeHashedSecret(manageSecret);

        // ShortURL 엔티티 생성
        ShortURL publishShortURL = makePublishShortURL(request, token, accessSecretHash, manageSecretHash);

        // 생성된 객체 Repository 전달
        log.info("Publish Service :: publishing :: ShortURL [{}] accessSecretHash? [{}] manageSecretHash? [{}]",
                publishShortURL, accessSecretHash != null, manageSecretHash != null);
        shortURLRepository.publish(publishShortURL);

        // 응답 반환
        return new PublishResult(token, manageSecret);
    }

    private @Nullable String computeAccessSecret(@NotNull PublishRequest request) {
        String accessSecret = request.getAccessSecret();
        if (!accessSecret.isEmpty())
            return accessSecret;
        return null;
    }

    private @Nullable String computeManangeSecret(@NotNull PublishRequest request) {
        if (request.getIsUsingManage())
            return createRandomManageSecret();
        return null;
    }

    private @Nullable String computeHashedSecret(@Nullable String secret) {
        if (secret == null)
            return null;
        return passwordEncoder.encode(secret);
    }

    private @NotNull ShortURL makePublishShortURL(@NotNull PublishRequest request,
                                                  @NotNull String token,
                                                  @Nullable String accessSecretHash,
                                                  @Nullable String manageSecretHash) {
        return ShortURL.builder()
                .token(token)
                .originalURL(request.getOriginalURL())
                .expireAfterSeconds(request.getExpireAfterSeconds())
                .expireAfterVisits(request.getExpireAfterVisits())
                .accessSecretHash(accessSecretHash)
                .manageSecretHash(manageSecretHash)
                .isActivated(manageSecretHash == null)
                .isForcefullyDowned(false)
                .build();
    }

    @Override
    public @NotNull ActivateResult activateURL(@NotNull ActivateRequest request) {
        String token = request.getToken();
        String manageSecret = request.getManageSecret();

        log.info("Publish Service :: activating :: token [{}]", token);
        ActivateResultType resultType = shortURLRepository.activate(token, manageSecret);
        return new ActivateResult(resultType);
    }

    @Override
    public @NotNull String createRandomManageSecret() {
        log.info("Publish Service :: creating random manage secret :: n/a");
        SecureRandom random = new SecureRandom();
        return random.ints(manageSecretLength, 0, MANAGE_SECRET_CHAR_POOL.length())
                .mapToObj(MANAGE_SECRET_CHAR_POOL::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
