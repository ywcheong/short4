package com.ywcheong.short4.controller.publish;

import com.ywcheong.short4.data.dto.PublishRequestDTO;
import com.ywcheong.short4.data.dto.PublishResponseDTO;
import com.ywcheong.short4.data.entity.ShortURL;
import com.ywcheong.short4.service.publish.PublishService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

// todo // 전역 Exception 핸들링

@Controller
@RequestMapping("/publish")
@Slf4j
public class PublishController {
    private final PublishService publishService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PublishController(PublishService publishService, PasswordEncoder passwordEncoder) {
        this.publishService = publishService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/new")
    public ResponseEntity<PublishResponseDTO> publishNewShortenURL(@Valid @RequestBody PublishRequestDTO requestDTO) {
        var builder = ShortURL.builder()
                .originalURL(requestDTO.getOriginalURL())
                .expireAfterSeconds(requestDTO.getExpireAfterSeconds())
                .expireAfterVisits(requestDTO.getExpireAfterVisits())
                .isForcefullyDowned(false);

        // salt, accessHash, <manageHash + isActivated> 처리
        String accessSecret = requestDTO.getAccessSecret().trim();

        if (!accessSecret.isEmpty()) {
            String accessSecretHash = passwordEncoder.encode(accessSecret);
            builder.accessSecretHash(accessSecretHash);
        } else {
            builder.accessSecretHash(null);
        }

        String manageSecret = null;
        if (requestDTO.getIsUsingManage()) {
            // 임의로 생성된 Manage Secret 구성
            manageSecret = publishService.generateRandomManageSecret();
            String manageSecretHash = passwordEncoder.encode(manageSecret);
            builder.manageSecretHash(manageSecretHash);
            builder.isActivated(false);
        } else {
            builder.manageSecretHash(null);
            builder.isActivated(true);
        }

        ShortURL publishShortURL = builder.build();
        log.info("Publish [{}] isAccessSecretSet [{}] isManageSet [{}]",
                publishShortURL, publishShortURL.getAccessSecretHash() == null, publishShortURL.getManageSecretHash() == null
        );

        boolean success = publishService.publishURL(publishShortURL);
        if (success) {
            PublishResponseDTO responseDTO = new PublishResponseDTO(manageSecret);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
