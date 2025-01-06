package com.ywcheong.short4.data.dto;

import com.ywcheong.short4.validator.HTTPKindURL;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PublishRequestDTO {
    // 단축할 원본URL
    @NotNull
    @HTTPKindURL
    private String originalURL;

    // 만료 설정
    @PositiveOrZero
    @Builder.Default
    private int expireAfterSeconds = 0;
    @PositiveOrZero
    @Builder.Default
    private int expireAfterVisits = 0;

    // 접근 비밀번호 설정시 제공
    @Size(max = 20)
    @ToString.Exclude
    @Builder.Default
    private String accessSecret = "";

    // 관리페이지 사용여부 // 사용 시 관리페이지 암호가 응답 본문에 추가
    @Builder.Default
    private Boolean isUsingManage = false;
}
