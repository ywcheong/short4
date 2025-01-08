package com.ywcheong.short4.service.publish;

import com.ywcheong.short4.repository.ShortURLRepository;
import com.ywcheong.short4.utility.TokenGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {DefaultReserveService.class,})
@ActiveProfiles("test")
@DisplayName("단위 테스트 :: ReserveService")
class DefaultReserveServiceTest {
    // 테스트 대상 객체의 의존성
    @MockitoBean
    private TokenGenerator mockTokenGenerator;
    @MockitoBean
    private ShortURLRepository mockRepository;
    @Autowired
    private ReserveService reserveService;

    @Nested
    @DisplayName("단위 테스트 :: reserveToken")
    class publishURLTest {

        @Test
        @DisplayName("토큰이 1회만에 유효하면 1회 시도만에 성공한다")
        public void oneTimeSuccess() {
            // Given...
            String sampleToken = "sample+token";

            given(mockTokenGenerator.generate("en-US")).willReturn(sampleToken);
            given(mockRepository.attemptReserve(sampleToken)).willReturn(true);

            // When...
            String reservedToken = reserveService.reserveToken("en-US");

            // Then...
            then(mockTokenGenerator).should().generate("en-US");
            then(mockRepository).should().attemptReserve(sampleToken);
            then(mockRepository).shouldHaveNoMoreInteractions();
            Assertions.assertEquals(sampleToken, reservedToken);
        }

        @Test
        @DisplayName("토큰이 2회만에 유효하면 2회 시도만에 성공한다")
        public void twoTimeSuccess() {
            // Given...
            String failToken = "fail+token";
            String sampleToken = "sample+token";

            given(mockTokenGenerator.generate("en-US")).willReturn(failToken).willReturn(sampleToken);
            given(mockRepository.attemptReserve(sampleToken)).willReturn(true);
            given(mockRepository.attemptReserve(failToken)).willReturn(false);

            // When...
            String reservedToken = reserveService.reserveToken("en-US");

            // Then...
            then(mockTokenGenerator).should(times(2)).generate("en-US");
            then(mockRepository).should().attemptReserve(failToken);
            then(mockRepository).should().attemptReserve(sampleToken);
            then(mockRepository).shouldHaveNoMoreInteractions();
            Assertions.assertEquals(sampleToken, reservedToken);
        }

        @Test
        @DisplayName("토큰이 계속 무효하면 실패한다")
        public void eventuallyFails() {
            // Given...
            String failToken = "fail+token";
            String sampleToken = "sample+token";

            given(mockTokenGenerator.generate("en-US")).willReturn(failToken);
            given(mockRepository.attemptReserve(sampleToken)).willReturn(true);
            given(mockRepository.attemptReserve(failToken)).willReturn(false);

            // When...
            // Then...
            Assertions.assertThrows(RuntimeException.class, () -> reserveService.reserveToken("en-US"));
        }

    }
}