package com.ywcheong.short4.controller.redirect;

import com.ywcheong.short4.data.dto.SimpleMessageResponse;
import com.ywcheong.short4.data.dto.redirect.RedirectResult;
import com.ywcheong.short4.service.redirect.RedirectService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RedirectController {
    private final RedirectService redirectService;

    @Autowired
    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<SimpleMessageResponse> redirectURL(@PathVariable("token") String token,
                                                             @Nullable @RequestParam(required = false) String accessSecret) {
        if (accessSecret == null || accessSecret.isEmpty()) {
            accessSecret = null;
        }

        log.info("Redirect Controller :: /{token} :: token [{}] accessSecret? [{}]", token, accessSecret != null);
        RedirectResult result = redirectService.redirect(token, accessSecret);

        return switch (result.getResultType()) {
            case SUCCESS -> ResponseEntity.status(HttpStatus.FOUND)
                    .location(result.getOriginalURI()).body(null);

            case TOKEN_NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new SimpleMessageResponse("주어진 토큰에 대응하는 단축URL은 없습니다.")
            );

            case WRONG_ACCESS_SECRET -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new SimpleMessageResponse("잘못된 접근 비밀번호입니다.")
            );
        };
    }
}
