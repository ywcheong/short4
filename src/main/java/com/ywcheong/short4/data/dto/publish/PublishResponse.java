package com.ywcheong.short4.data.dto.publish;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PublishResponse {
    String token;
    @ToString.Exclude
    String manageSecret;
}
