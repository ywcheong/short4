package com.ywcheong.short4.controller.publish;

import com.ywcheong.short4.data.dto.PublishNewRequestDTO;
import com.ywcheong.short4.data.dto.PublishNewResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/publish")
public class PublishShortenURLController {
    @PostMapping("/new")
    public ResponseEntity<PublishNewResponseDTO> publishNewShortenURL(@RequestBody PublishNewRequestDTO requestDTO) {
        // todo // PublishNewRequestDTO 검증 로직
        // todo // 전역 Exception 핸들링
        return ResponseEntity.status(HttpStatus.OK).body(new PublishNewResponseDTO());
    }
}
