package com.ywcheong.short4.controller.publish;

import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.service.publish.PublishService;
import com.ywcheong.short4.validator.HTTPKindURLValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PublishController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class
        }
)
@ActiveProfiles("test")
@ContextConfiguration(classes = {HTTPKindURLValidator.class, PublishController.class})
@DisplayName("단위 테스트 :: PublishController")
public class PublishControllerTest {

    @MockitoBean
    PublishService publishService;
    @MockitoBean
    PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mvc;

    private void simpleRequest(String originalURL) throws Exception {
        // Given...
        given(
                publishService.publishURL(argThat(
                        dto -> dto != null && Objects.equals(dto.getOriginalURL(), originalURL)
                ))
        ).willReturn(
                ShortURL.builder().originalURL(originalURL).build()
        );

        // When...
        mvc.perform(post("/publish/new").content("""
                        {"originalURL":"%s"}""".formatted(originalURL)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isCreated(), jsonPath("$.manageSecret").isEmpty());

        // Then...
        then(passwordEncoder).shouldHaveNoInteractions();
        then(publishService).should().publishURL(argThat(argument -> argument.getOriginalURL().equals(originalURL)));
        then(publishService).shouldHaveNoMoreInteractions();
    }

    private void simpleWrongRequest(String originalURL) throws Exception {
        // Given...
        // When...
        mvc.perform(post("/publish/new").content("""
                        {"originalURL":"%s"}""".formatted(originalURL)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());

        // Then...
        then(passwordEncoder).shouldHaveNoInteractions();
        then(publishService).shouldHaveNoInteractions();
    }

    @Nested
    @DisplayName("유효한 요청 세트를 받는다")
    class ValidRequestSet {
        @Test
        @DisplayName("http 프로토콜을 받는다")
        public void httpDefault() throws Exception {
            simpleRequest("http://example.com");
            simpleRequest("hTtP://example.com");
        }

        @Test
        @DisplayName("https 프로토콜을 받는다")
        public void httpsDefault() throws Exception {
            simpleRequest("https://example.com");
            simpleRequest("HtTpS://example.com");
        }
    }

    @Nested
    @DisplayName("잘못된 요청 세트를 거부한다")
    class InvalidRequestSet {
        @Test
        @DisplayName("지원하지 않는 프로토콜을 거부한다")
        public void unsupportedProtocol() throws Exception {
            simpleWrongRequest("telnet://example.com");
        }

        @Test
        @DisplayName("URL이 아닌 문자열을 거부한다")
        public void notURL() throws Exception {
            simpleWrongRequest("wefjoiejfro;23@@@!!");
        }
    }

}
