package com.ywcheong.short4.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PublishNewRequestDTO {
    // 단축할 원본URL
    private final String originalURL;

    // 만료 설정
    private int expireAfterSeconds = 0;
    private int expireAfterVisits = 0;

    // 접근 비밀번호 설정시 제공
    private String accessSecret = "";

    // 관리페이지 사용여부 // 사용 시 관리페이지 암호가 응답 본문에 추가
    private Boolean isUsingManage = false;
}
