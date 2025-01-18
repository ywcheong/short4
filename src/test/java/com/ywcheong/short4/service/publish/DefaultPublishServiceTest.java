package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.config.SecurityConfig;
import com.ywcheong.short4.data.dto.publish.PublishRequest;
import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.repository.ShortURLRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {
        DefaultPublishService.class,
        PasswordEncoder.class,
        SecurityConfig.class
})
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("test")
@DisplayName("단위 테스트 :: DefaultPublishService")
class DefaultPublishServiceTest {
    private final String originalURL = "https://example.com";
    @MockitoBean
    ReserveService mockReserveService;
    @MockitoBean
    ShortURLRepository mockShortURLRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    DefaultPublishService publishService;

    @Value("${short4.server.manage-secret-length}")
    private int manageSecretLength;

    @Nested
    @DisplayName("단위 테스트 :: publishURL")
    class publishURLTest {
        @Test
        @DisplayName("accessSecret과 isUsingManage이 설정되지 않은 경우를 처리한다")
        public void testNoSecret() {
            // Given...
            given(
                    mockReserveService.reserveToken("en-US")
            ).willReturn(
                    "123456"
            );

            given(
                    mockShortURLRepository.publish(any(ShortURL.class))
            ).willReturn(
                    any(ShortURL.class)
            );

            // When...
            PublishRequest requestDTO = PublishRequest.builder()
                    .originalURL(originalURL)
                    .expireAfterSeconds(1234)
                    .expireAfterVisits(5678)
                    .accessSecret("")
                    .isUsingManage(false)
                    .build();

            publishService.publishURL(requestDTO);

            // Then...
            then(mockShortURLRepository).should().publish(argThat(shortURL ->
                    shortURL.getOriginalURL().equals(originalURL)
                            && shortURL.getExpireAfterSeconds() == 1234
                            && shortURL.getExpireAfterVisits() == 5678
                            && shortURL.getToken().equals("123456")
                            && shortURL.getAccessSecretHash() == null
                            && shortURL.getManageSecretHash() == null
                            && shortURL.getIsActivated()
                            && !shortURL.getIsForcefullyDowned()
            ));
            then(mockReserveService).should().reserveToken("en-US");
        }

        @Test
        @DisplayName("accessSecret과 isUsingManage이 설정된 경우를 처리한다")
        public void testFullSecret() {
            // Given...
            given(
                    mockReserveService.reserveToken("en-US")
            ).willReturn(
                    "123456"
            );

            given(
                    mockShortURLRepository.publish(any(ShortURL.class))
            ).willReturn(
                    any(ShortURL.class)
            );

            // When...
            PublishRequest requestDTO = PublishRequest.builder()
                    .originalURL(originalURL)
                    .expireAfterSeconds(1234)
                    .expireAfterVisits(5678)
                    .accessSecret("access-secret")
                    .isUsingManage(true)
                    .build();

            publishService.publishURL(requestDTO);

            // Then...
            then(mockShortURLRepository).should().publish(argThat(this::testFullSecretMatcher));
            then(mockReserveService).should().reserveToken("en-US");
        }

        private boolean testFullSecretMatcher(ShortURL shortURL) {
            System.out.println("shortURL: " + shortURL.toString());

            if (!(shortURL.getOriginalURL().equals(originalURL)
                    && shortURL.getExpireAfterSeconds() == 1234
                    && shortURL.getExpireAfterVisits() == 5678
                    && shortURL.getToken().equals("123456")
                    && !shortURL.getIsActivated()
                    && !shortURL.getIsForcefullyDowned()
            )) {
                System.out.println("Basic condition violation");
                return false;
            }

            String accessSecretHash = shortURL.getAccessSecretHash();
            if (!passwordEncoder.matches("access-secret", accessSecretHash)) {
                System.out.println("accessSecretHash: " + accessSecretHash);
                System.out.println("Access secret inconsistency");
                return false;
            }

            String manageSecretHash = shortURL.getManageSecretHash();
            if (manageSecretHash == null || manageSecretHash.isBlank()) {
                System.out.println("manageSecretHash is null or blank (isnull = " + (manageSecretHash == null) + ")");
                System.out.println("Manage secret invalid");
                return false;
            }

            return true;
        }
    }

    @Nested
    @DisplayName("단위 테스트 :: createRandomManageSecret")
    class generateRandomManageSecretTest {
        @Test
        @DisplayName("랜덤한 manageSecret을 생성한다")
        public void manageSecretTest() {
            String randomSecret = publishService.createRandomManageSecret();
            System.out.println("generated secret: " + randomSecret);
            Assertions.assertEquals(manageSecretLength, randomSecret.length());
        }
    }
}