package com.ywcheong.short4.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActivateRequestDTO {
    @NotNull(message = "필수 필드입니다.")
    private String token;

    @ToString.Exclude
    @NotNull(message = "필수 필드입니다.")
    private String manageSecret;
}
