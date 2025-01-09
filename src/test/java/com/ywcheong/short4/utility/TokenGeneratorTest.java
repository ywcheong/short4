package com.ywcheong.short4.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
        TokenGenerator.class, WordDictionary.class
})
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("test")
@DisplayName("단위 테스트 :: TokenGenerator")
public class TokenGeneratorTest {
    @Autowired
    private TokenGenerator tokenGenerator;

    @Nested
    @DisplayName("단위 테스트 :: Generate")
    class generateUnitTest {
        private static final String PATTERN = "^[a-zA-Z]+(\\+[a-zA-Z]+)*$";

        public static boolean isValidTokenPattern(String input) {
            return input != null && input.matches(PATTERN);
        }

        @Test
        @DisplayName("올바른 토큰을 생성한다")
        public void generateGoodToken() {
            for (int i = 0; i < 20; i++) {
                String token = tokenGenerator.generate("en-US");
                Assertions.assertTrue(isValidTokenPattern(token));
            }
        }
    }
}
