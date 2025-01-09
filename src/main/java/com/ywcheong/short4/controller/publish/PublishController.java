package com.ywcheong.short4.controller.publish;

import com.ywcheong.short4.data.dto.*;
import com.ywcheong.short4.data.entity.ShortURL;
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
    public ResponseEntity<PublishResponseDTO> publishNewShortenURL(@Validated @RequestBody PublishRequestDTO requestDTO) {
        log.info("Publish Controller :: /publish/new :: [{}]", requestDTO);
        ShortURL resultShortURL = publishService.publishURL(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PublishResponseDTO(resultShortURL.getManageSecretHash()));
    }

    @PostMapping("/activate")
    public ResponseEntity<SimpleMessageResponseDTO> activateShortURL(@Validated @RequestBody ActivateRequestDTO requestDTO) {
        log.info("Publish Controller :: /publish/activate :: [{}]", requestDTO);
        ActivateResult result = publishService.activateURL(requestDTO);

        return switch (result.getResult()) {
            case SUCCESS -> ResponseEntity.status(HttpStatus.OK).body(
                    new SimpleMessageResponseDTO("성공")
            );

            case ALREADY_ACTIVATED -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    new SimpleMessageResponseDTO("주어진 토큰에 대응하는 단축URL은 이미 활성화되어 있습니다.")
            );

            case TOKEN_NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new SimpleMessageResponseDTO("주어진 토큰에 대응하는 단축URL은 없습니다.")
            );

            case WRONG_MANAGE_SECRET -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new SimpleMessageResponseDTO("잘못된 관리 비밀번호입니다.")
            );
        };
    }
}
