package com.ywcheong.short4.data.dto;

import com.ywcheong.short4.validator.HTTPKindURL;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotNull(message = "필수 필드입니다.")
    @HTTPKindURL(message = "HTTP 및 HTTPS 프로토콜만 입력할 수 있습니다.")
    private String originalURL;

    // 만료 설정
    @PositiveOrZero(message = "0보다 큰 정수여야 합니다.")
    @Builder.Default
    private int expireAfterSeconds = 0;
    @PositiveOrZero(message = "0보다 큰 정수여야 합니다.")
    @Builder.Default
    private int expireAfterVisits = 0;

    // 접근 비밀번호 설정시 제공
    @Size(max = 20, message = "20자 이하여야 합니다.")
    @ToString.Exclude
    @Builder.Default
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()]*$",
            message = "대소문자 알파벳, 숫자 및 !@#$%^&*()에 해당하는 특수문자로만 구성되어야 합니다."
    )
    private String accessSecret = "";

    // 관리페이지 사용여부 // 사용 시 관리페이지 암호가 응답 본문에 추가
    @Builder.Default
    private Boolean isUsingManage = false;
}
