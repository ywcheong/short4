package com.ywcheong.short4.controller.publish;

import com.ywcheong.short4.data.dto.SimpleMessageResponse;
import com.ywcheong.short4.data.dto.publish.*;
import com.ywcheong.short4.service.publish.PublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// todo // 전역 Exception 핸들링

@RestController
@RequestMapping("/publish")
@Slf4j
public class PublishController {
    private final PublishService publishService;

    @Autowired
    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/new")
    public ResponseEntity<PublishResponse> newURL(@Validated @RequestBody PublishRequest request) {
        log.info("Publish Controller :: /publish/new :: [{}]", request);
        PublishResult response = publishService.publishURL(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/activate")
    public ResponseEntity<SimpleMessageResponse> activateURL(@Validated @RequestBody ActivateRequest request) {
        log.info("Publish Controller :: /publish/activate :: [{}]", request);
        ActivateResult result = publishService.activateURL(request);

        return switch (result.getResultType()) {
            case SUCCESS -> ResponseEntity.status(HttpStatus.OK).body(
                    new SimpleMessageResponse("성공")
            );

            case ALREADY_ACTIVATED -> ResponseEntity.status(HttpStatus.ACCEPTED).body(
                    new SimpleMessageResponse("주어진 토큰에 대응하는 단축URL은 이미 활성화되어 있습니다.")
            );

            case TOKEN_NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new SimpleMessageResponse("주어진 토큰에 대응하는 단축URL은 없습니다.")
            );

            case WRONG_MANAGE_SECRET -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new SimpleMessageResponse("잘못된 관리 비밀번호입니다.")
            );
        };
    }
}
