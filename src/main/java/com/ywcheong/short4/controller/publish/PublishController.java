package com.ywcheong.short4.controller.publish;

import com.ywcheong.short4.data.dto.PublishRequestDTO;
import com.ywcheong.short4.data.dto.PublishResponseDTO;
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
        log.info("Publish Controller :: Request received :: [{}]", requestDTO);
        ShortURL resultShortURL = publishService.publishURL(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PublishResponseDTO(resultShortURL.getManageSecretHash()));
    }
}
