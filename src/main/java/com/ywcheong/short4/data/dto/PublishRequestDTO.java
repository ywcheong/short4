package com.ywcheong.short4.data.dto;

import com.ywcheong.short4.validator.HTTPKindURL;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class PublishRequestDTO {
    // 단축할 원본URL
    @NotNull
    @HTTPKindURL
    private final String originalURL;

    // 만료 설정
    @Positive
    private int expireAfterSeconds = 0;
    @Positive
    private int expireAfterVisits = 0;

    // 접근 비밀번호 설정시 제공
    @Size(max = 20)
    @ToString.Exclude
    private String accessSecret = "";

    // 관리페이지 사용여부 // 사용 시 관리페이지 암호가 응답 본문에 추가
    private Boolean isUsingManage = false;
}
