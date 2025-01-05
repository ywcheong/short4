package com.ywcheong.short4.data.dto;

import com.ywcheong.short4.validator.HTTPKindURL;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PublishRequestDTO {
    // 단축할 원본URL
    @NotNull
    @HTTPKindURL
    private String originalURL;

    // 만료 설정
    @PositiveOrZero
    private int expireAfterSeconds = 0;
    @PositiveOrZero
    private int expireAfterVisits = 0;

    // 접근 비밀번호 설정시 제공
    @Size(max = 20)
    @ToString.Exclude
    private String accessSecret = "";

    // 관리페이지 사용여부 // 사용 시 관리페이지 암호가 응답 본문에 추가
    private Boolean isUsingManage = false;
}
