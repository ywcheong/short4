package com.ywcheong.short4.data.dto.publish;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublishResponse {
    String token;
    String manageSecret;
}
